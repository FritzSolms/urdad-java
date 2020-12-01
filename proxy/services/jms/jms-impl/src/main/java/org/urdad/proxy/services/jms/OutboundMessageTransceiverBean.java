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

package org.urdad.proxy.services.jms;

import com.google.common.base.Strings;
import java.util.UUID;
import java.util.concurrent.Future;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.events.services.ServiceRequestSubmittedEvent;
import org.urdad.events.services.ServiceResultNotReceivedEvent;
import org.urdad.events.services.ServiceResultReceivedEvent;
import org.urdad.events.services.UnexpectedServiceProviderErrorEvent;
import org.urdad.proxy.jms.MessageConstructor;
import org.urdad.proxy.jms.PayloadCompressor;
import org.urdad.proxy.jms.PayloadDecompressor;
import org.urdad.proxy.jms.PayloadTransformer;
import org.urdad.proxy.jms.PropertySetter;
import org.urdad.proxy.jms.PropertySetterRequestContext;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;
import org.urdad.services.mask.ServiceMask;
import org.urdad.validation.services.ServiceValidationUtilities;

/**
 * TODO: Javadoc
 */
public class OutboundMessageTransceiverBean extends MessageTransceiverBean implements OutboundMessageTransceiver
    .OutboundMessageTransceiverLocal, OutboundMessageTransceiver.OutboundMessageTransceiverRemote
{

    public OutboundMessageTransceiverBean(Class serviceProviderType, ServiceValidationUtilities serviceValidationUtilities)
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

        this.outboundMessageTransceiverConfiguration = configureRequest.getOutboundMessageTransceiverConfiguration();
        this.messageTransceiverConfiguration = configureRequest.getOutboundMessageTransceiverConfiguration();

        // Generate message transceiver name is one has not be specified.
        if (Strings.isNullOrEmpty(outboundMessageTransceiverConfiguration.getMessageTransceiverName()))
        {
            outboundMessageTransceiverConfiguration.setMessageTransceiverName(generateMessageTransceiverName());
        }

        // Create service response.
        return new ConfigureResponse();
    }

    @Override
    public Response invokeService(Request
        request) throws Throwable
    {
        logger.trace("Service request received.");

        // Check pre-condition: Request must be valid.
        serviceValidationUtilities.validateRequest(Request.class, request);

        // Start transceiver.
        startTransceiver();

        try
        {
            // Create request correlation ID.
            String requestCorrelationId = UUID.randomUUID().toString();

            logger.trace("Generated correlation ID '" + requestCorrelationId + "'");

            try
            {
                // Expose request identifier (if required).
                if (outboundMessageTransceiverConfiguration.getRequestIdentifierExposer() != null)
                {
                    outboundMessageTransceiverConfiguration.getRequestIdentifierExposer().exposeRequestIdentifier(new
                        RequestIdentifierExposer.ExposeRequestIdentifierRequest(requestCorrelationId,
                        request));
                }

                Object requestPayload = request;

                // Transform payload (if required).
                PayloadTransformer transmitterPayloadTransformer = outboundMessageTransceiverConfiguration
                    .getTransmitterPayloadTransformer();
                if (transmitterPayloadTransformer != null)
                {
                    logger.trace("Transforming request message payload.");
                    try
                    {
                        requestPayload = transmitterPayloadTransformer.transformPayload(new PayloadTransformer.TransformPayloadRequest
                            (requestPayload)).getPayload();
                    }
                    catch (PayloadTransformer.PayloadMustBeAmenableToTransformationException e)
                    {
                        // System error.
                        throw new RuntimeException(e);
                    }
                }

                // Compress payload (if required).
                PayloadCompressor transmitterPayloadCompressor = outboundMessageTransceiverConfiguration.getTransmitterPayloadCompressor();
                if (transmitterPayloadCompressor != null)
                {
                    logger.trace("Compressing request message payload.");

                    requestPayload = transmitterPayloadCompressor.compressPayload(new PayloadCompressor.CompressPayloadRequest(requestPayload))
                        .getCompressedPayload();
                }

                // Construct the JMS request message.
                Message requestMessage;
                try
                {
                    logger.trace("Constructing request message.");
                    requestMessage = outboundMessageTransceiverConfiguration.getMessageConstructor().constructMessage(new MessageConstructor
                        .ConstructMessageRequest(transmitterSession, requestCorrelationId, outboundMessageTransceiverConfiguration
                        .getReceiverDestination(), requestPayload)).getMessage();
                }
                catch (RequestNotValidException | MessageConstructor.PayloadTypeNotSupportedException e)
                {
                    // System error.
                    throw new RuntimeException(e);
                }

                // Apply service provider type JMS message property if required.
                if (outboundMessageTransceiverConfiguration.getApplyServiceProviderTypeProperty())
                {
                    logger.trace("Applying service provider type '" + serviceProviderType.getName() + "'.");
                    requestMessage.setStringProperty("serviceProviderType", serviceProviderType.getName());
                }

                // Apply service provider identifier JMS message property if required.
                if (outboundMessageTransceiverConfiguration.getApplyServiceProviderIdentifierProperty())
                {
                    if (!Strings.isNullOrEmpty(outboundMessageTransceiverConfiguration.getServiceProviderIdentifier()))
                    {
                        logger.trace("Applying service provider identifier '" + outboundMessageTransceiverConfiguration
                            .getServiceProviderIdentifier() + "'.");
                        requestMessage.setStringProperty("serviceProviderIdentifier", outboundMessageTransceiverConfiguration
                            .getServiceProviderIdentifier());
                    }
                }

                // Set any user-defined JMS message properties (if required).
                for (PropertySetter propertySetter : outboundMessageTransceiverConfiguration.getPropertySetters())
                {
                    logger.trace("Applying message property setter '" + propertySetter.getClass().getName() + "'.");
                    try
                    {
                        PropertySetterRequestContext propertySetterRequestContext = new PropertySetterRequestContext();
                        propertySetterRequestContext.setRequest(request);
                        requestMessage = propertySetter.setProperty(new PropertySetter.SetPropertyRequest
                            (propertySetterRequestContext, requestMessage)).getMessage();

                        logger.trace("Message property setter '" + propertySetter.getClass().getName() + "' applied.");
                    }
                    catch (RequestNotValidException e)
                    {
                        // System error.
                        throw new RuntimeException(e);
                    }
                }

                logger.trace("Transmitting request message.");

                // Transmit the request message.
                try
                {
                    transmitterMessageProducer.send(requestMessage);
                    // Publish event.
                    ServiceRequestSubmittedEvent serviceRequestSubmittedEvent = constructServiceEvent
                        (ServiceRequestSubmittedEvent.class, request);
                    serviceRequestSubmittedEvent.setRequestIdentifier(requestCorrelationId);
                    publishEvent(serviceRequestSubmittedEvent);
                }
                catch (JMSException e)
                {
                    // System error.
                    throw new RuntimeException("Unable to transmit the request message.", e);
                }

            }
            catch (RuntimeException e)
            {
                logger.error(e.getMessage(), e);
                throw e;
            }

            try
            {
                logger.trace("Waiting for response message.");

                // Wait for response message.
                Message responseMessage;
                try
                {
                    // Create new message consumer;
                    if (outboundMessageTransceiverConfiguration.getSelectByCorrelationIdAndResultIndicator())
                    {
                        logger.trace("Attempting to create message consumer with message selector based on response type and correlation ID.");

                        createReceiverMessageConsumer("JMSCorrelationID = '" + requestCorrelationId + "' AND result = 'true'");
                    }
                    else
                    {
                        logger.trace("Attempting to create message consumer with message selector based on correlation ID.");

                        createReceiverMessageConsumer("JMSCorrelationID = '" + requestCorrelationId + "'");
                    }

                    responseMessage = receiverMessageConsumer.receive(outboundMessageTransceiverConfiguration
                        .getReceiverMessageConsumptionTimeout());
                }
                catch (JMSException e)
                {
                    // System error.
                    throw new RuntimeException("Unable to receive the response message.", e);
                }
                finally
                {
                    closeReceiverMessageConsumer();
                }

                // Check pre-condition: Response message must be received before timeout.
                if (responseMessage == null)
                {
                    // Publish event.
                    ServiceResultNotReceivedEvent serviceResultNotReceivedEvent = constructServiceEvent
                        (ServiceResultNotReceivedEvent.class, request);
                    serviceResultNotReceivedEvent.setElaboration("Response not received before timeout of " +
                        outboundMessageTransceiverConfiguration.getReceiverMessageConsumptionTimeout() +
                        " milliseconds.");
                    serviceResultNotReceivedEvent.setRequestIdentifier(requestCorrelationId);
                    publishEvent(serviceResultNotReceivedEvent);

                    throw new ResponseNotReceivedBeforeTimeoutError("Response message not received before timeout.");
                }

                logger.trace("Received response message.");

                logger.trace("Extracting response message payload.");

                // Extract response payload.
                Object responsePayload;

                try
                {
                    if (responseMessage instanceof ObjectMessage)
                    {
                        logger.trace("Object message detected.");
                        responsePayload = ((ObjectMessage) responseMessage).getObject();
                    }
                    else if (responseMessage instanceof BytesMessage)
                    {
                        logger.trace("Byte message detected.");

                        byte[] bytes = new byte[(int) ((BytesMessage) responseMessage).getBodyLength()];
                        ((BytesMessage) responseMessage).readBytes(bytes);
                        responsePayload = bytes;
                    }
                    else if (responseMessage instanceof TextMessage)
                    {
                        logger.trace("Text message detected.");
                        responsePayload = ((TextMessage) responseMessage).getText();

                        if (Strings.isNullOrEmpty((String) responsePayload))
                        {
                            throw new RuntimeException("Response message payload is null.");
                        }
                    }
                    else
                    {
                        // System error.
                        throw new RuntimeException("Invalid response message type received. Messages of type '" + responseMessage
                            .getClass().getName() + "' are not currently supported.");
                    }
                }
                catch (JMSException e)
                {
                    // System error.
                    throw new RuntimeException(e);
                }

                logger.trace("Checking response message correlation ID.");

                String responseCorrelationId;
                try
                {
                    responseCorrelationId = responseMessage.getJMSCorrelationID();
                }
                catch (JMSException e)
                {
                    // System error.
                    throw new RuntimeException("Unable to extract correlation ID from response message.", e);
                }

                if (Strings.isNullOrEmpty(responseCorrelationId))
                {
                    // System error.
                    throw new RuntimeException("Response message does not contain a correlation ID.");
                }
                if (!responseCorrelationId.equals(requestCorrelationId))
                {
                    // System error.
                    throw new RuntimeException("Response message's correlation ID does not match the request message's " +
                        "correlation ID.");
                }

                // Decompress payload (if required).
                PayloadDecompressor receiverPayloadDecompressor = outboundMessageTransceiverConfiguration.getReceiverPayloadDecompressor();

                if (receiverPayloadDecompressor != null)
                {
                    logger.trace("Decompressing response message payload.");

                    try
                    {
                        responsePayload = receiverPayloadDecompressor.decompressPayload(new PayloadDecompressor.DecompressPayloadRequest
                            ((byte[]) responsePayload)).getPayload();
                    }
                    catch (ClassCastException e)
                    {
                        throw new RuntimeException("Response payload must be of type byte[] to be eligible for decompression");
                    }
                    catch (RequestNotValidException e)
                    {
                        // System error.
                        throw new RuntimeException(e);
                    }
                }

                // Transform payload (if required).
                PayloadTransformer receiverPayloadTransformer = outboundMessageTransceiverConfiguration
                    .getReceiverPayloadTransformer();
                if (receiverPayloadTransformer != null)
                {
                    logger.trace("Transforming response message payload.");

                    try
                    {
                        responsePayload = receiverPayloadTransformer.transformPayload(new PayloadTransformer.TransformPayloadRequest
                            (responsePayload)).getPayload();
                    }
                    catch (RequestNotValidException | PayloadTransformer.PayloadMustBeAmenableToTransformationException e)
                    {
                        // System error.
                        throw new RuntimeException(e);
                    }
                }

                logger.trace("Confirming whether payload is a service response or throwable.");

                // Create service response.
                Response response ;

                if (responsePayload instanceof Response)
                {
                    response=((Response) responsePayload);
                }
                else if (responsePayload instanceof Throwable)
                {
                    throw (Throwable) responsePayload;
                }
                else
                {
                    // System error.
                    throw new RuntimeException("Response message's payload should either be a service response object or throwable.");
                }

                // Publish event.
                ServiceResultReceivedEvent serviceResultReceivedEvent = constructServiceEvent
                    (ServiceResultReceivedEvent.class, request);
                serviceResultReceivedEvent.setRequestIdentifier(requestCorrelationId);
                publishEvent(serviceResultReceivedEvent);

                commitReceiverSessionIfTransacted();

                return response;
            }
            catch (Throwable t)
            {
                logger.error(t.getMessage(), t);
                rollbackReceiverSessionIfTransacted();

                // Publish event.
                UnexpectedServiceProviderErrorEvent unexpectedServiceProviderErrorEvent =
                    constructServiceProviderEvent(UnexpectedServiceProviderErrorEvent.class, this);
                unexpectedServiceProviderErrorEvent.setElaboration(t.getMessage());
                publishEvent(unexpectedServiceProviderErrorEvent);

                throw t;
            }
        }
        finally
        {
            // Stop transceiver.
            stopTransceiver();
        }
    }

    @Override
    public Future<Response> invokeServiceAsync(Request request) throws Throwable {
        throw new UnsupportedOperationException("OutboundMessageTransceiverBean does not support async invocation");
    }

    @Override
    public ServiceMask.RetrieveServiceProviderTypeResponse retrieveServiceProviderType(ServiceMask.RetrieveServiceProviderTypeRequest
        retrieveServiceProviderTypeRequest)
    {
        return new ServiceMask.RetrieveServiceProviderTypeResponse(serviceProviderType);
    }

    private void startTransceiver()
    {
        logger.trace("Starting transceiver.");

        if (transceiverStarted)
        {
            // System error.
            throw new RuntimeException("Transceiver is already started.");
        }

        if (outboundMessageTransceiverConfiguration == null)
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

            logger.trace("Attempting to startTransceiver receiver and transmitter connections and sessions.");

            // Start connections.
            startReceiverConnection();
            startTransmitterConnection();

            logger.trace("Receiver and transmitter connections and sessions started.");

            // Create message receiver and producer.

            logger.trace("Attempting to create message producer. Message receiver created for each response.");

            // Create a message producer that is responsible for the transmission of responses or throwables.
            createTransmitterMessageProducer();

            logger.trace("Message producer created.");

            transceiverStarted = true;
        }
        catch (Exception e)
        {
            try
            {
                stopTransceiver();
            }
            catch(RuntimeException e1)
            {
                // Ignore. Allow original exception to be thrown.
            }

            throw e;
        }
    }

    private void stopTransceiver()
    {
        logger.trace("Stopping transceiver.");

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

    private static final Logger unwrappedLogger = LoggerFactory.getLogger(OutboundMessageTransceiverBean.class);
    // Configuration must be explicitly set. Required instance for usage scenario not easy to qualify.
    @NotNull(message = "An outbound message transceiver configuration must be specified.")
    private OutboundMessageTransceiverConfiguration outboundMessageTransceiverConfiguration;
    private Boolean transceiverStarted = false;

}