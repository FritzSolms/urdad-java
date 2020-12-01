/*
 * Copyright 2019 Dr. Fritz Solms & Craig Edwards
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.urdad.proxy.events.jms;

import com.google.common.base.Strings;
import java.util.UUID;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.urdad.events.Event;
import org.urdad.events.proxy.EventPublisherProxy;
import org.urdad.proxy.jms.MessageConsumptionGovernor;
import org.urdad.proxy.jms.PayloadDecompressor;
import org.urdad.proxy.jms.PayloadTransformer;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;

/**
 * TODO: Javadoc
 */
public class InboundMessageReceiverBean implements InboundMessageReceiver.InboundMessageReceiverLocal,
	InboundMessageReceiver.InboundMessageReceiverRemote
{

	public InboundMessageReceiverBean(ServiceValidationUtilities serviceValidationUtilities)
	{
		if (serviceValidationUtilities == null)
		{
			throw new RuntimeException("A service validation utilities must be specified.");
		}

		this.serviceValidationUtilities = serviceValidationUtilities;

		logger = new LoggerWrapper(unwrappedLogger);
	}

	@Override
	public ConfigureResponse configure(ConfigureRequest configureRequest) throws RequestNotValidException
	{
		unwrappedLogger.debug("Configuring receiver.'");

		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(ConfigureRequest.class, configureRequest);

		inboundMessageReceiverConfiguration = configureRequest.getInboundMessageReceiverConfiguration();

		// Generate message receiver name is one has not be specified.
		if (Strings.isNullOrEmpty(inboundMessageReceiverConfiguration.getMessageReceiverName()))
		{
			inboundMessageReceiverConfiguration.setMessageReceiverName(generateMessageReceiverName());
		}

		// Create service response.
		return new ConfigureResponse();
	}

	@Override
	public StartMonitoringResponse startMonitoring(StartMonitoringRequest startMonitoringRequest) throws
		RequestNotValidException, ReceiverIsMonitoringException
	{
		logger.trace("Monitoring started.");

		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(StartMonitoringRequest.class, startMonitoringRequest);

		// Check pre-condition: Receiver must not be started.
		if (receiverMonitoring)
		{
			throw new ReceiverIsMonitoringException("Receiver is already monitoring.");
		}

		receiverMonitoring = true;

		inboundMessageReceiverConfiguration.getExecutorService().execute(new RequestMonitor());

		// Create service response.
		return new StartMonitoringResponse();
	}

	@Override
	public StopMonitoringResponse stopMonitoring(StopMonitoringRequest stopMonitoringRequest) throws
		RequestNotValidException, ReceiverIsNotMonitoringException
	{
		logger.trace("Monitoring stopped.");

		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(StopMonitoringRequest.class, stopMonitoringRequest);

		// Check pre-condition: TransReceiverceiver must be started.
		if (!receiverStarted)
		{
			throw new ReceiverIsNotMonitoringException("Receiver is not monitoring.");
		}

		receiverMonitoring = false;

		// Create service response.
		return new StopMonitoringResponse();
	}

	@Override
	public CheckMonitoringResponse checkMonitoring(CheckMonitoringRequest checkMonitoringRequest) throws RequestNotValidException
	{
		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(CheckMonitoringRequest.class, checkMonitoringRequest);

		// Create service response.
		return new CheckMonitoringResponse(receiverMonitoring);
	}

	private class RequestMonitor implements Runnable
	{

		@Override
		public void run()
		{
			logger.trace("Starting to monitoring for events.");

			try
			{
				// Start receiver.
				startReceiver();

				while (receiverMonitoring)
				{
					MessageConsumptionGovernor messageConsumptionGovernor = inboundMessageReceiverConfiguration
						.getMessageConsumptionGovernor();

					if (messageConsumptionGovernor != null)
					{
						try
						{
							if (!messageConsumptionGovernor.consumptionPermitted(new MessageConsumptionGovernor
								.ConsumptionPermittedRequest()).getPermitted())
							{
								logger.trace("Event consumption not permitted!");

                                rest(inboundMessageReceiverConfiguration.getEventMonitoringRestPeriod());
								continue;
							}
						}
						catch (RequestNotValidException e)
						{
						    // System error. (Should not happen)
							throw new RuntimeException(e);
						}

						// logger.trace("Event consumption permitted!");
					}

					try
					{
						// Wait for event message.
						Message eventMessage;
						try
						{
							eventMessage = receiverMessageConsumer.receive(inboundMessageReceiverConfiguration
								.getReceiverMessageConsumptionTimeout());
						}
						catch (JMSException e)
						{
							// System error.
							throw new RuntimeException("Unable to receive the event message.", e);
						}

						// Confirm whether message has been received.
						if (eventMessage == null)
						{
							continue;
						}
						else
						{
							logger.trace("Event message received.");
						}

						logger.trace("Extracting event message payload.");

						// Extract event payload.
						Object eventPayload;

						try
						{
							if (eventMessage instanceof ObjectMessage)
							{
								logger.trace("Object message detected.");
								eventPayload = ((ObjectMessage) eventMessage).getObject();
							}
							else if (eventMessage instanceof BytesMessage)
							{
								logger.trace("Byte message detected.");

								byte[] bytes = new byte[(int) ((BytesMessage) eventMessage).getBodyLength()];
								((BytesMessage) eventMessage).readBytes(bytes);
								eventPayload = bytes;
							}
							else if (eventMessage instanceof TextMessage)
							{
								logger.trace("Text message detected.");
								eventPayload = ((TextMessage) eventMessage).getText();

								if (Strings.isNullOrEmpty((String) eventPayload))
								{
									throw new RuntimeException("Message payload is null.");
								}
							}
							else
							{
								// System error.
								throw new RuntimeException("Invalid event message type received. Messages of type '"
								    + eventMessage.getClass().getName() + "' are not currently supported.");
							}
						}
						catch (JMSException e)
						{
							// System error.
							throw new RuntimeException(e);
						}

						// Decompress payload (if required).
						PayloadDecompressor receiverPayloadDecompressor = inboundMessageReceiverConfiguration
						    .getReceiverPayloadDecompressor();

						if (receiverPayloadDecompressor != null)
						{
							logger.trace("Decompressing event message payload.");

							try
							{
								eventPayload = receiverPayloadDecompressor.decompressPayload(new PayloadDecompressor
                                    .DecompressPayloadRequest((byte[]) eventPayload)).getPayload();
							}
							catch (ClassCastException e)
							{
								throw new RuntimeException("Payload must be of type byte[] to be eligible for " +
									"decompression");
							}
							catch (RequestNotValidException e)
							{
								// System error.
								throw new RuntimeException(e);
							}
						}

						// Transform payload (if required).
						PayloadTransformer receiverPayloadTransformer = inboundMessageReceiverConfiguration
								.getReceiverPayloadTransformer();
						if (receiverPayloadTransformer != null)
						{
							logger.trace("Transforming event message payload.");
							try
							{
								eventPayload = receiverPayloadTransformer.transformPayload(new PayloadTransformer
                                    .TransformPayloadRequest(eventPayload)).getPayload();
							}
							catch (RequestNotValidException | PayloadTransformer
								.PayloadMustBeAmenableToTransformationException e)
							{
								// System error.
								throw new RuntimeException(e);
							}
						}

						logger.trace("Confirming whether payload is an event.");

						if (!Event.class.isInstance(eventPayload))
						{
							// System error.
							throw new RuntimeException("Message payload must be an event.");
						}

						commitReceiverSessionIfTransacted();
						logger.trace("Publishing event.");

						try
						{
							inboundMessageReceiverConfiguration.getEventPublisherProxy().publishEvent(new
                                EventPublisherProxy.PublishEventRequest((Event) eventPayload));
						}
						catch (Throwable t)
						{
							logger.error("Unhandled Throwable during event publishing: {}",t);
						}

					}
					catch (RuntimeException e)
					{
						rollbackReceiverSessionIfTransacted();

						logger.error(e.getMessage(), e);

						rest(inboundMessageReceiverConfiguration.getEventMonitoringRestPeriod());
					}
				}
			}
			catch(Throwable t)
			{
				logger.error(t.getMessage(), t);

				throw t;
			}
			finally
			{
				receiverMonitoring = false;

				// Stop receiver.
				stopReceiver();

				logger.trace("Stopped monitoring for events.");
			}
		}
	}

	private void startReceiver()
	{
		logger.trace("Starting receiver.");

		// Check pre-condition: Receiver must not be started.
		if (receiverStarted)
		{
			// System error.
			throw new RuntimeException("Receiver is already started.");
		}

		// Check pre-condition: Receiver must be configured.
		if (inboundMessageReceiverConfiguration == null)
		{
			// System error.
			throw new RuntimeException("Receiver has not been configured.");
		}

		try
		{
			// Create connection and session.

			logger.trace("Attempting to create receiver connection and session.");

			createReceiverConnection();
			createReceiverSession();

			logger.trace("Receiver connection and session created.");

			logger.trace("Attempting to start monitoring receiver connection and session.");

			// Start connection.
			startReceiverConnection();

			logger.trace("Receiver connection and session started.");

			// Create message receiver.

			logger.trace("Attempting to create message receiver.");

			// Build request message selector.
			StringBuilder requestMessageSelector = new StringBuilder();

			if (!Strings.isNullOrEmpty(inboundMessageReceiverConfiguration.getEventMessageSelector()))
			{
				requestMessageSelector.append(inboundMessageReceiverConfiguration.getEventMessageSelector());
			}

			logger.trace("Setting message selector '" + requestMessageSelector.toString() + "' on receiver " +
				"message consumer.");

			// Create a message consumer that is responsible for the receipt of events.
			if (requestMessageSelector.length() > 0)
			{
				createReceiverMessageConsumer(inboundMessageReceiverConfiguration.getReceiverDurableConsumer(),
					requestMessageSelector.toString());
			}
			else
			{
				createReceiverMessageConsumer(inboundMessageReceiverConfiguration.getReceiverDurableConsumer());
			}

			logger.trace("Message receiver created.");

			receiverStarted = true;
		}
		catch (Exception e)
		{
			try
			{
				stopReceiver();
			}
			catch (RuntimeException e1)
			{
				// Ignore. Allow original exception to be thrown.
			}

			throw e;
		}
	}

	private void stopReceiver()
	{
		logger.trace("Stopping receiver.");

		// Check pre-condition: Receiver must be started.
		if (!receiverStarted)
		{
			// System error.
			throw new RuntimeException("Receiver is not started.");
		}

		// Close message receiver.

		logger.trace("Attempting to close message receiver.");

		closeReceiverMessageConsumer();

		logger.trace("Message receiver closed.");

		// Close connection and session.

		logger.trace("Attempting to close receiver connection and session.");

		closeReceiverSession();
		closeReceiverConnection();

		logger.trace("Receiver connection and session closed.");

		receiverStarted = false;
	}

	private String generateMessageReceiverName()
	{
		logger.trace("Generating message receiver name.");
		return UUID.randomUUID().toString();
	}

	private void rest(Long timeout)
	{
		try
		{
			logger.trace("Resting for '" + timeout + "' milliseconds.");
			Thread.sleep(timeout);
		}
		catch (InterruptedException e)
		{
			// System error. (Should not happen)
			throw new RuntimeException(e);
		}
	}

	private void createReceiverConnection()
	{
		try
		{
			logger.trace("Creating receiver connection.");
			receiverConnection = inboundMessageReceiverConfiguration.getReceiverConnectionFactory().createConnection();

			if (inboundMessageReceiverConfiguration.getSetReceiverConnectionClientId())
			{
				receiverConnection.setClientID(inboundMessageReceiverConfiguration.getMessageReceiverName() + "_Receiver");

				logger.trace("Receiver connection client ID = '" + receiverConnection.getClientID() + "'");
			}
			else
			{
				logger.trace("Receiver connection client ID not set.");
			}

		}
		catch (JMSException e)
		{
			// System error.
			throw new RuntimeException("Unable to create JMS connection for receiver.", e);
		}
	}

	private void startReceiverConnection()
	{
		try
		{
			logger.trace("Starting receiver connection.");
			receiverConnection.start();
		}
		catch (JMSException e)
		{
			// System error.
			throw new RuntimeException("Unable to start monitoring JMS connection for receiver.", e);
		}
	}

	private void closeReceiverConnection()
	{
		try
		{
			logger.trace("Closing receiver connection.");
			receiverConnection.close();
		}
		catch (JMSException e)
		{
			// System error.
			throw new RuntimeException("Unable to close JMS connection for receiver.", e);
		}
	}

	private void createReceiverSession()
	{
		try
		{
			if (inboundMessageReceiverConfiguration.getReceiverTransacted())
			{
				logger.trace("Creating transacted receiver session.");
				receiverSession = receiverConnection.createSession(true, Session.SESSION_TRANSACTED);
			}
			else
			{
				logger.trace("Creating non-transacted receiver session.");
				receiverSession = receiverConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			}
		}
		catch (JMSException e)
		{
			// System error.
			throw new RuntimeException("Unable to create JMS session for receiver.", e);
		}
	}

	private void commitReceiverSessionIfTransacted()
	{
		try
		{
			if (receiverSession.getTransacted())
			{
				logger.trace("Committing receiver session.");
				receiverSession.commit();
			}
		}
		catch (JMSException e)
		{
			// System error.
			throw new RuntimeException("Unable to commit JMS session for receiver.", e);
		}
	}

	private void rollbackReceiverSessionIfTransacted()
	{
		try
		{
			if (receiverSession.getTransacted())
			{
				logger.warn("Rolling back receiver session.");
				receiverSession.rollback();
			}
		}
		catch (JMSException e)
		{
			// System error.
			throw new RuntimeException("Unable to rollback JMS session for receiver.", e);
		}
	}

	private void closeReceiverSession()
	{
		try
		{
			logger.trace("Closing receiver session.");
			receiverSession.close();
		}
		catch (JMSException e)
		{
			// System error.
			throw new RuntimeException("Unable to close JMS session for receiver.", e);
		}
	}

	/** Create a message consumer that is responsible for the receipt of messages. */
	private void createReceiverMessageConsumer(Boolean durable)
	{
		if (durable)
		{
			try
			{
				logger.trace("Creating durable receiver message consumer.");
				receiverMessageConsumer = receiverSession.createDurableSubscriber(inboundMessageReceiverConfiguration
					.getReceiverTopic(), inboundMessageReceiverConfiguration.getMessageReceiverName());
			}
			catch (JMSException e)
			{
				// System error.
				throw new RuntimeException("Unable to create JMS message consumer for receiver.", e);
			}
		}
		else
		{
			try
			{
				logger.trace("Creating receiver message consumer.");
				receiverMessageConsumer = receiverSession.createConsumer(inboundMessageReceiverConfiguration
					.getReceiverTopic());
			}
			catch (JMSException e)
			{
				// System error.
				throw new RuntimeException("Unable to create JMS message consumer for receiver.", e);
			}
		}
	}
	/** Create a message consumer that is responsible for the receipt of messages. */
	private void createReceiverMessageConsumer(Boolean durable, String messageSelector)
	{
		if (durable)
		{
			try
			{
				logger.trace("Creating durable receiver message consumer with message selector '" +
					messageSelector + "'.");
				receiverMessageConsumer = receiverSession.createDurableSubscriber(inboundMessageReceiverConfiguration
					.getReceiverTopic(), inboundMessageReceiverConfiguration.getMessageReceiverName(), messageSelector,
					true);
			}
			catch (JMSException e)
			{
				// System error.
				throw new RuntimeException("Unable to create JMS message consumer for receiver.", e);
			}
		}
		else
		{
			try
			{
				logger.trace("Creating receiver message consumer with message selector '" + messageSelector + "'.");
				receiverMessageConsumer = receiverSession.createConsumer(inboundMessageReceiverConfiguration
					.getReceiverTopic(), messageSelector);
			}
			catch (JMSException e)
			{
				// System error.
				throw new RuntimeException("Unable to create JMS message consumer for receiver.", e);
			}
		}
	}

	/** TODO: Javadoc */
	private void closeReceiverMessageConsumer()
	{
		try
		{
			logger.trace("Closing receiver message consumer.");

			if (receiverMessageConsumer != null)
			{
				receiverMessageConsumer.close();
			}
		}
		catch (JMSException e)
		{
			// System error.
			throw new RuntimeException("Unable to close JMS message consumer for receiver.", e);
		}
	}

	private class LoggerWrapper implements org.slf4j.Logger
	{

		public LoggerWrapper(Logger logger)
		{
			this.logger = logger;
		}

		@Override
		public boolean isTraceEnabled()
		{
			return this.logger.isTraceEnabled();
		}

		@Override
		public void trace(String msg)
		{
			logger.trace(inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg);
		}

		@Override
		public void trace(String format, Object arg)
		{
			logger.trace(format, arg);
		}

		@Override
		public void trace(String format, Object arg1, Object arg2)
		{
			logger.trace(format, arg1, arg2);
		}

		@Override
		public void trace(String format, Object... arguments)
		{
			logger.trace(format, arguments);
		}

		@Override
		public void trace(String msg, Throwable t)
		{
			logger.trace(inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg, t);
		}

		@Override
		public boolean isTraceEnabled(Marker marker)
		{
			return logger.isTraceEnabled(marker);
		}

		@Override
		public void trace(Marker marker, String msg)
		{
			logger.trace(marker, inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg);
		}

		@Override
		public void trace(Marker marker, String format, Object arg)
		{
			logger.trace(marker, format, arg);
		}

		@Override
		public void trace(Marker marker, String format, Object arg1, Object arg2)
		{
			logger.trace(marker, format, arg1, arg2);
		}

		@Override
		public void trace(Marker marker, String format, Object... argArray)
		{
			logger.trace(marker, format, argArray);
		}

		@Override
		public void trace(Marker marker, String msg, Throwable t)
		{
			logger.trace(marker, inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg, t);
		}

		@Override
		public boolean isDebugEnabled()
		{
			return logger.isDebugEnabled();
		}

		@Override
		public void debug(String msg)
		{
			logger.trace(inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg);
		}

		@Override
		public void debug(String format, Object arg)
		{
			logger.trace(format, arg);
		}

		@Override
		public void debug(String format, Object arg1, Object arg2)
		{
			logger.trace(format, arg1, arg2);
		}

		@Override
		public void debug(String format, Object... arguments)
		{
			logger.trace(format, arguments);
		}

		@Override
		public void debug(String msg, Throwable t)
		{
			logger.trace(inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg, t);
		}

		@Override
		public boolean isDebugEnabled(Marker marker)
		{
			return logger.isDebugEnabled(marker);
		}

		@Override
		public void debug(Marker marker, String msg)
		{
			logger.trace(marker, inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg);
		}

		@Override
		public void debug(Marker marker, String format, Object arg)
		{
			logger.trace(marker, format, arg);
		}

		@Override
		public void debug(Marker marker, String format, Object arg1, Object arg2)
		{
			logger.trace(marker, format, arg1, arg2);
		}

		@Override
		public void debug(Marker marker, String format, Object... arguments)
		{
			logger.trace(marker, format, arguments);
		}

		@Override
		public void debug(Marker marker, String msg, Throwable t)
		{
			logger.trace(marker, inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg, t);
		}

		@Override
		public boolean isInfoEnabled()
		{
			return logger.isInfoEnabled();
		}

		@Override
		public void info(String msg)
		{
			logger.info(inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg);
		}

		@Override
		public void info(String format, Object arg)
		{
			logger.info(format, arg);
		}

		@Override
		public void info(String format, Object arg1, Object arg2)
		{
			logger.info(format, arg1, arg2);
		}

		@Override
		public void info(String format, Object... arguments)
		{
			logger.info(format, arguments);
		}

		@Override
		public void info(String msg, Throwable t)
		{
			logger.info(inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg, t);
		}

		@Override
		public boolean isInfoEnabled(Marker marker)
		{
			return logger.isInfoEnabled(marker);
		}

		@Override
		public void info(Marker marker, String msg)
		{
			logger.info(marker, inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg);
		}

		@Override
		public void info(Marker marker, String format, Object arg)
		{
			logger.info(marker, format, arg);
		}

		@Override
		public void info(Marker marker, String format, Object arg1, Object arg2)
		{
			logger.info(marker, format, arg1, arg2);
		}

		@Override
		public void info(Marker marker, String format, Object... arguments)
		{
			logger.info(marker, format, arguments);
		}

		@Override
		public void info(Marker marker, String msg, Throwable t)
		{
			logger.info(marker, inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg, t);
		}

		@Override
		public boolean isWarnEnabled()
		{
			return logger.isWarnEnabled();
		}

		@Override
		public void warn(String msg)
		{
			logger.warn(inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg);
		}

		@Override
		public void warn(String format, Object arg)
		{
			logger.warn(format, arg);
		}

		@Override
		public void warn(String format, Object... arguments)
		{
			logger.warn(format, arguments);
		}

		@Override
		public void warn(String format, Object arg1, Object arg2)
		{
			logger.warn(format, arg1, arg2);
		}

		@Override
		public void warn(String msg, Throwable t)
		{
			logger.warn(inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg, t);
		}

		@Override
		public boolean isWarnEnabled(Marker marker)
		{
			return logger.isWarnEnabled(marker);
		}

		@Override
		public void warn(Marker marker, String msg)
		{
			logger.warn(marker, inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg);
		}

		@Override
		public void warn(Marker marker, String format, Object arg)
		{
			logger.warn(marker, format, arg);
		}

		@Override
		public void warn(Marker marker, String format, Object arg1, Object arg2)
		{
			logger.warn(marker, format, arg1, arg2);
		}

		@Override
		public void warn(Marker marker, String format, Object... arguments)
		{
			logger.warn(marker, format, arguments);
		}

		@Override
		public void warn(Marker marker, String msg, Throwable t)
		{
			logger.warn(marker, inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg, t);
		}

		@Override
		public boolean isErrorEnabled()
		{
			return logger.isErrorEnabled();
		}

		@Override
		public void error(String msg)
		{
			logger.error(inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg);
		}

		@Override
		public void error(String format, Object arg)
		{
			logger.error(format, arg);
		}

		@Override
		public void error(String format, Object arg1, Object arg2)
		{
			logger.error(format, arg1, arg2);
		}

		@Override
		public void error(String format, Object... arguments)
		{
			logger.error(format, arguments);
		}

		@Override
		public void error(String msg, Throwable t)
		{
			logger.error(inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg, t);
		}

		@Override
		public boolean isErrorEnabled(Marker marker)
		{
			return logger.isErrorEnabled(marker);
		}

		@Override
		public void error(Marker marker, String msg)
		{
			logger.error(marker, inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg);
		}

		@Override
		public void error(Marker marker, String format, Object arg)
		{
			logger.error(marker, format, arg);
		}

		@Override
		public void error(Marker marker, String format, Object arg1, Object arg2)
		{
			logger.error(marker, format, arg1, arg2);
		}

		@Override
		public void error(Marker marker, String format, Object... arguments)
		{
			logger.error(marker, format, arguments);
		}

		@Override
		public void error(Marker marker, String msg, Throwable t)
		{
			logger.error(marker, inboundMessageReceiverConfiguration.getMessageReceiverName() + ": " + msg, t);
		}

		@Override
		public String getName()
		{
			return logger.getName();
		}

		private Logger logger;
	}

	protected LoggerWrapper logger;
	private static final Logger unwrappedLogger = LoggerFactory.getLogger(InboundMessageReceiverBean.class);
	protected ServiceValidationUtilities serviceValidationUtilities;
	protected Connection receiverConnection;
	protected Session receiverSession;
	protected MessageConsumer receiverMessageConsumer;
	// Configuration must be explicitly set. Required instance for usage scenario not easy to qualify.
	@NotNull(message = "An inbound message receiver configuration must be specified.")
	private InboundMessageReceiverConfiguration inboundMessageReceiverConfiguration;
	private Boolean receiverMonitoring = false;
	private Boolean receiverStarted = false;

}