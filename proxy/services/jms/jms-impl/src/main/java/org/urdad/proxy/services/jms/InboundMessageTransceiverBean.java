/* Copyright 2019 Dr. Fritz Solms & Craig Edwards Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required
 * by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License. */

package org.urdad.proxy.services.jms;

import com.google.common.base.Strings;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.events.services.NoPendingServiceRequestEvent;
import org.urdad.events.services.ServiceRequestReceivedEvent;
import org.urdad.events.services.ServiceResultSubmittedEvent;
import org.urdad.events.services.UnexpectedServiceProviderErrorEvent;
import org.urdad.proxy.jms.MessageConstructor;
import org.urdad.proxy.jms.MessageConsumptionGovernor;
import org.urdad.proxy.jms.PayloadCompressor;
import org.urdad.proxy.jms.PayloadDecompressor;
import org.urdad.proxy.jms.PayloadTransformer;
import org.urdad.services.mask.ServiceMask;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.UnexpectedServiceError;
import org.urdad.validation.services.ServiceValidationUtilities;

/**
 * TODO: Javadoc
 */
public class InboundMessageTransceiverBean extends MessageTransceiverBean
		implements InboundMessageTransceiver.InboundMessageTransceiverLocal, InboundMessageTransceiver.InboundMessageTransceiverRemote
{

	public InboundMessageTransceiverBean(Class serviceProviderType, ServiceValidationUtilities serviceValidationUtilities)
	{
		if (serviceProviderType == null)
		{
			throw new RuntimeException("A service provider type must be specified.");
		}

		this.serviceProviderType = serviceProviderType;

		if (serviceValidationUtilities == null)
		{
			throw new RuntimeException("A service validation utilities must be specified.");
		}

		this.serviceValidationUtilities = serviceValidationUtilities;

		super.logger = new LoggerWrapper(unwrappedLogger);
	}

	@Override
	public ConfigureResponse configure(ConfigureRequest configureRequest) throws RequestNotValidException
	{
		unwrappedLogger.debug("Configuring transceiver.'");

		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(ConfigureRequest.class, configureRequest);

		this.inboundMessageTransceiverConfiguration = configureRequest.getInboundMessageTransceiverConfiguration();
		this.messageTransceiverConfiguration = configureRequest.getInboundMessageTransceiverConfiguration();

		// Generate message transceiver name is one has not be specified.
		if (Strings.isNullOrEmpty(inboundMessageTransceiverConfiguration.getMessageTransceiverName()))
		{
			inboundMessageTransceiverConfiguration.setMessageTransceiverName(generateMessageTransceiverName());
		}

		// Create service response.
		return new ConfigureResponse();
	}

	@Override
	public StartMonitoringResponse startMonitoring(StartMonitoringRequest startMonitoringRequest)
			throws RequestNotValidException, TransceiverIsMonitoringException
	{
		logger.trace("Monitoring started.");

		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(StartMonitoringRequest.class, startMonitoringRequest);

		// Check pre-condition: Transceiver must not be started.
		if (transceiverMonitoring)
		{
			throw new TransceiverIsMonitoringException("Transceiver is already monitoring.");
		}

		transceiverMonitoring = true;

		inboundMessageTransceiverConfiguration.getExecutorService().execute(new RequestMonitor());

		// Create service response.
		return new StartMonitoringResponse();
	}

	@Override
	public StopMonitoringResponse stopMonitoring(StopMonitoringRequest stopMonitoringRequest)
			throws RequestNotValidException, TransceiverIsNotMonitoringException
	{
		logger.trace("Monitoring stopped.");

		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(StopMonitoringRequest.class, stopMonitoringRequest);

		// Check pre-condition: Transceiver must be started.
		if (!transceiverStarted)
		{
			throw new TransceiverIsNotMonitoringException("Transceiver is not monitoring.");
		}

		transceiverMonitoring = false;

		// Create service response.
		return new StopMonitoringResponse();
	}

	@Override
	public CheckMonitoringResponse checkMonitoring(CheckMonitoringRequest checkMonitoringRequest) throws RequestNotValidException
	{
		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(CheckMonitoringRequest.class, checkMonitoringRequest);

		// Create service response.
		return new CheckMonitoringResponse(transceiverMonitoring);
	}

	private class RequestMonitor implements Runnable
	{

		@Override
		public void run()
		{
			logger.trace("Starting to monitoring for requests.");

			try
			{
				// Start transceiver.
				startTransceiver();

				while (transceiverMonitoring)
				{
					MessageConsumptionGovernor messageConsumptionGovernor = inboundMessageTransceiverConfiguration
							.getMessageConsumptionGovernor();

					if (messageConsumptionGovernor != null)
					{
						try
						{
							if (!messageConsumptionGovernor
									.consumptionPermitted(new MessageConsumptionGovernor.ConsumptionPermittedRequest()).getPermitted())
							{
								logger.trace("Request consumption not permitted!");

								rest(inboundMessageTransceiverConfiguration.getRequestMonitoringRestPeriod());
								continue;
							}
						} catch (RequestNotValidException e)
						{
							// System error. (Should not happen)
							throw new RuntimeException(e);
						}

						// logger.trace("Request consumption permitted!");
					}


					Object requestPayload;
					String requestCorrelationId;
					
					try
					{
						// Wait for request message.
						Message requestMessage;
						try
						{
							requestMessage = receiverMessageConsumer
									.receive(inboundMessageTransceiverConfiguration.getReceiverMessageConsumptionTimeout());
						} catch (JMSException e)
						{
							// System error.
							throw new RuntimeException("Unable to receive the request message.", e);
						}

						// Confirm whether request has been received.
						if (requestMessage == null)
						{
							// Publish event.
							NoPendingServiceRequestEvent noPendingServiceRequestEvent = constructServiceProviderEvent(
									NoPendingServiceRequestEvent.class, inboundMessageTransceiverConfiguration.getServiceMask());
							publishEvent(noPendingServiceRequestEvent);

							continue;
						}
						else
						{
							logger.trace("Request message received.");
						}

						logger.trace("Checking correlation ID.");

						
						try
						{
							requestCorrelationId = requestMessage.getJMSCorrelationID();
						} catch (JMSException e)
						{
							// System error.
							throw new RuntimeException("Unable to extract correlation ID from request message.", e);
						}

						if (Strings.isNullOrEmpty(requestCorrelationId))
						{
							// System error.
							throw new RuntimeException("Request message does not contain a correlation ID.");
						}

						logger.trace("Extracting request message payload.");

						// Extract request payload.

						try
						{
							if (requestMessage instanceof ObjectMessage)
							{
								logger.trace("Object message detected.");
								requestPayload = ((ObjectMessage) requestMessage).getObject();
							}
							else if (requestMessage instanceof BytesMessage)
							{
								logger.trace("Byte message detected.");

								byte[] bytes = new byte[(int) ((BytesMessage) requestMessage).getBodyLength()];
								((BytesMessage) requestMessage).readBytes(bytes);
								requestPayload = bytes;
							}
							else if (requestMessage instanceof TextMessage)
							{
								logger.trace("Text message detected.");
								requestPayload = ((TextMessage) requestMessage).getText();

								if (Strings.isNullOrEmpty((String) requestPayload))
								{
									throw new RuntimeException("Request message payload is null.");
								}
							}
							else
							{
								// System error.
								throw new RuntimeException("Invalid request message type received. Messages of type '"
										+ requestMessage.getClass().getName() + "' are not currently supported.");
							}
						} catch (JMSException e)
						{
							// System error.
							throw new RuntimeException(e);
						}

						// Decompress payload (if required).
						PayloadDecompressor receiverPayloadDecompressor = inboundMessageTransceiverConfiguration
								.getReceiverPayloadDecompressor();

						if (receiverPayloadDecompressor != null)
						{
							logger.trace("Decompressing request message payload.");

							try
							{
								requestPayload = receiverPayloadDecompressor
										.decompressPayload(new PayloadDecompressor.DecompressPayloadRequest((byte[]) requestPayload))
										.getPayload();
							} catch (ClassCastException e)
							{
								throw new RuntimeException("Request payload must be of type byte[] to be eligible for decompression");
							} catch (RequestNotValidException e)
							{
								// System error.
								throw new RuntimeException(e);
							}
						}

						// Transform payload (if required).
						PayloadTransformer receiverPayloadTransformer = inboundMessageTransceiverConfiguration
								.getReceiverPayloadTransformer();
						if (receiverPayloadTransformer != null)
						{
							logger.trace("Transforming request message payload.");
							try
							{
								requestPayload = receiverPayloadTransformer
										.transformPayload(new PayloadTransformer.TransformPayloadRequest(requestPayload)).getPayload();
							} catch (RequestNotValidException | PayloadTransformer.PayloadMustBeAmenableToTransformationException e)
							{
								// System error.
								throw new RuntimeException(e);
							}
						}

						logger.trace("Confirming whether payload is a service request.");

						if (!Request.class.isInstance(requestPayload))
						{
							// System error.
							throw new RuntimeException("Message must be a service request.");
						}

						commitReceiverSessionIfTransacted();

					} catch (RuntimeException e)
					{
						rollbackReceiverSessionIfTransacted();

						logger.error(e.getMessage(), e);

						rest(inboundMessageTransceiverConfiguration.getRequestMonitoringRestPeriod());

						continue;
					}
					try
					{
						// Publish event.
						ServiceRequestReceivedEvent serviceRequestReceivedEvent = constructServiceEvent(ServiceRequestReceivedEvent.class,
								((Request) requestPayload));
						serviceRequestReceivedEvent.setRequestIdentifier(requestCorrelationId);
						publishEvent(serviceRequestReceivedEvent);

						logger.trace("Executing service.");

						Object responsePayload;

						try
						{
							responsePayload = inboundMessageTransceiverConfiguration.getServiceMask()
									.invokeService(((Request) requestPayload));
						} catch (Throwable t)
						{
							if (t instanceof RuntimeException)
							{
								if (Strings.isNullOrEmpty(t.getMessage()) && t.getCause() != null
										&& t.getCause() instanceof RuntimeException)
								{
									// Create default URDAD representation of an unexpected 'system' error.
									// - Provides more fine grained control over serialization to textual formats such as XML.
									// - Eliminate stack trace and reduces size of response payload.
									responsePayload = new UnexpectedServiceError(t.getCause().getMessage());
								}
								else
								{
									// Create default URDAD representation of an unexpected 'system' error.
									// - Provides more fine grained control over serialization to textual formats such as XML.
									// - Eliminate stack trace and reduces size of response payload.
									responsePayload = new UnexpectedServiceError(t.getMessage());
								}
							}
							else
							{
								responsePayload = t;
							}
						}

						logger.trace("Transforming response payload.");

						// Transform payload (if required).
						PayloadTransformer transmitterPayloadTransformer = inboundMessageTransceiverConfiguration
								.getTransmitterPayloadTransformer();
						if (transmitterPayloadTransformer != null)
						{
							try
							{
								responsePayload = transmitterPayloadTransformer
										.transformPayload(new PayloadTransformer.TransformPayloadRequest(responsePayload)).getPayload();
							} catch (RequestNotValidException | PayloadTransformer.PayloadMustBeAmenableToTransformationException e)
							{
								// System error.
								throw new RuntimeException(e);
							}
						}

						// Compress payload (if required).
						PayloadCompressor transmitterPayloadCompressor = inboundMessageTransceiverConfiguration
								.getTransmitterPayloadCompressor();
						if (transmitterPayloadCompressor != null)
						{
							logger.trace("Compressing response message payload.");

							try
							{
								responsePayload = transmitterPayloadCompressor
										.compressPayload(new PayloadCompressor.CompressPayloadRequest(responsePayload))
										.getCompressedPayload();
							} catch (RequestNotValidException e)
							{
								// System error.
								throw new RuntimeException(e);
							}
						}

						logger.trace("Constructing response message.");

						// Construct the JMS response message.
						Message responseMessage;

						try
						{
							responseMessage = inboundMessageTransceiverConfiguration.getMessageConstructor()
									.constructMessage(new MessageConstructor.ConstructMessageRequest(transmitterSession,
											requestCorrelationId, responsePayload))
									.getMessage();
						} catch (RequestNotValidException | MessageConstructor.PayloadTypeNotSupportedException e)
						{
							// System error.
							throw new RuntimeException(e);
						}

						// Apply result indicator if required.
						if (inboundMessageTransceiverConfiguration.getApplyResultIndicatorProperty())
						{
							try
							{
								responseMessage.setStringProperty("result", "true");
							} catch (JMSException e)
							{
								// System error.
								throw new RuntimeException("Unable to apply result indicator property.", e);
							}
						}

						logger.trace("Transmitting response message.");

						// Transmit the response message.
						try
						{
							transmitterMessageProducer.send(responseMessage);
						} catch (JMSException e)
						{
							// System error.
							throw new RuntimeException("Unable to transmit the response message.", e);
						}

						// Publish event.
						ServiceResultSubmittedEvent serviceResultSubmittedEvent = constructServiceEvent(ServiceResultSubmittedEvent.class,
								((Request) requestPayload));
						serviceResultSubmittedEvent.setRequestIdentifier(requestCorrelationId);
						publishEvent(serviceResultSubmittedEvent);
						

					} catch (RuntimeException e)
					{
						logger.error(e.getMessage(), e);

						rest(inboundMessageTransceiverConfiguration.getRequestMonitoringRestPeriod());

					}
				}
			} catch (Throwable t)
			{
				// Publish event.
				UnexpectedServiceProviderErrorEvent unexpectedServiceProviderErrorEvent = constructServiceProviderEvent(
						UnexpectedServiceProviderErrorEvent.class, inboundMessageTransceiverConfiguration.getServiceMask());
				unexpectedServiceProviderErrorEvent.setElaboration(t.getMessage());
				publishEvent(unexpectedServiceProviderErrorEvent);

				logger.error(t.getMessage(), t);

				throw t;
			} finally
			{
				transceiverMonitoring = false;

				// Stop transceiver.
				stopTransceiver();

				logger.trace("Stopped monitoring for requests.");
			}
		}
	}

	private void startTransceiver()
	{
		logger.trace("Starting transceiver.");

		// Check pre-condition: Transceiver must not be started.
		if (transceiverStarted)
		{
			// System error.
			throw new RuntimeException("Transceiver is already started.");
		}

		// Check pre-condition: Transceiver must be configured.
		if (inboundMessageTransceiverConfiguration == null)
		{
			// System error.
			throw new RuntimeException("Transceiver has not been configured.");
		}

		try
		{
			// Create connections and sessions.

			logger.trace("Attempting to create receiver and transmitter connections and sessions.");

			createReceiverConnection();
			createTransmitterConnection();
			createReceiverSession();
			createTransmitterSession();

			logger.trace("Receiver and transmitter connections and sessions created.");

			logger.trace("Attempting to startMonitoring receiver and transmitter connections and sessions.");

			// Start connections.
			startReceiverConnection();
			startTransmitterConnection();

			logger.trace("Receiver and transmitter connections and sessions started.");

			// Create message receiver and producer.

			logger.trace("Attempting to create message receiver and producer.");

			// Build request message selector.
			StringBuilder requestMessageSelector = new StringBuilder();

			if (inboundMessageTransceiverConfiguration.getSelectByServiceProviderTypeProperty())
			{
				requestMessageSelector.append("serviceProviderType = '").append(serviceProviderType.getName()).append("'");
				;
			}

			if (inboundMessageTransceiverConfiguration.getOnlySelectByServiceProviderIdentifierProperty())
			{
				if (requestMessageSelector.length() > 0)
				{
					requestMessageSelector.append(" AND ");
				}

				requestMessageSelector.append("serviceProviderIdentifier = '")
						.append(inboundMessageTransceiverConfiguration.getServiceProviderIdentifier()).append("'");

			}
			else if (inboundMessageTransceiverConfiguration
					.getSelectByServiceProviderIdentifierPropertyAndNoServiceProviderIdentifierProperty())
			{
				if (requestMessageSelector.length() > 0)
				{
					requestMessageSelector.append(" AND ");
				}

				requestMessageSelector.append("(serviceProviderIdentifier = '")
						.append(inboundMessageTransceiverConfiguration.getServiceProviderIdentifier())
						.append("' OR serviceProviderIdentifier IS NULL)");
			}
			else
			{
				if (requestMessageSelector.length() > 0)
				{
					requestMessageSelector.append(" AND ");
				}

				requestMessageSelector.append("serviceProviderIdentifier IS NULL");
			}

			if (!Strings.isNullOrEmpty(inboundMessageTransceiverConfiguration.getRequestMessageSelector()))
			{
				if (requestMessageSelector.length() > 0)
				{
					requestMessageSelector.append(" AND ");
				}

				requestMessageSelector.append(inboundMessageTransceiverConfiguration.getRequestMessageSelector());
			}

			logger.trace("Setting message selector '" + requestMessageSelector.toString() + "' on receiver message consumer.");

			// Create a message consumer that is responsible for the receipt of requests.
			if (requestMessageSelector.length() > 0)
			{
				createReceiverMessageConsumer(requestMessageSelector.toString());
			}
			else
			{
				createReceiverMessageConsumer();
			}

			// Create a message producer that is responsible for the transmission of responses or throwables.
			createTransmitterMessageProducer();

			logger.trace("Message receiver and producer created.");

			transceiverStarted = true;
		} catch (Exception e)
		{
			try
			{
				stopTransceiver();
			} catch (RuntimeException e1)
			{
				// Ignore. Allow original exception to be thrown.
			}

			throw e;
		}
	}

	private void stopTransceiver()
	{
		logger.trace("Stopping transceiver.");

		// Check pre-condition: Transceiver must be started.
		if (!transceiverStarted)
		{
			// System error.
			throw new RuntimeException("Transceiver is not started.");
		}

		// Close message receiver and producer.

		logger.trace("Attempting to close message receiver and producer.");

		closeReceiverMessageConsumer();
		closeTransmitterMessageProducer();

		logger.trace("Message receiver and producer closed.");

		// Close connections and sessions.

		logger.trace("Attempting to close receiver and transmitter connections and sessions.");

		closeTransmitterSession();
		closeTransmitterConnection();
		closeReceiverSession();
		closeReceiverConnection();

		logger.trace("Receiver and transmitter connections and sessions closed.");

		transceiverStarted = false;
	}

	private static final Logger unwrappedLogger = LoggerFactory.getLogger(InboundMessageTransceiverBean.class);
	// Configuration must be explicitly set. Required instance for usage scenario not easy to qualify.
	@NotNull(message = "An inbound message transceiver configuration must be specified.")
	private InboundMessageTransceiverConfiguration inboundMessageTransceiverConfiguration;
	private Boolean transceiverMonitoring = false;
	private Boolean transceiverStarted = false;

}