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
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.urdad.events.proxy.EventPublisherProxy;
import org.urdad.proxy.jms.MessageConstructor;
import org.urdad.proxy.jms.PayloadCompressor;
import org.urdad.proxy.jms.PayloadTransformer;
import org.urdad.proxy.jms.PropertySetter;
import org.urdad.proxy.jms.PropertySetterEventContext;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;

/**
 * TODO: Javadoc
 */
public class OutboundMessageTransmitterBean implements OutboundMessageTransmitter.OutboundMessageTransmitterLocal, 
    OutboundMessageTransmitter.OutboundMessageTransmitterRemote
{

    public OutboundMessageTransmitterBean(ServiceValidationUtilities serviceValidationUtilities)
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
        unwrappedLogger.debug("Configuring transmitter.'");

        // Check pre-condition: Request must be valid.
        serviceValidationUtilities.validateRequest(ConfigureRequest.class, configureRequest);

        this.outboundMessageTransmitterConfiguration = configureRequest.getOutboundMessageTransmitterConfiguration();
       
        // Generate message transmitter name is one has not be specified.
        if (Strings.isNullOrEmpty(outboundMessageTransmitterConfiguration.getMessageTransmitterName()))
        {
            outboundMessageTransmitterConfiguration.setMessageTransmitterName(generateMessageTransmitterName());
        }

        // Create service response.
        return new ConfigureResponse();
    }

    @Override
    public EventPublisherProxy.PublishEventResponse publishEvent(EventPublisherProxy.PublishEventRequest
        publishEventRequest) throws Throwable
    {
        logger.trace("Event received.");

        // Check pre-condition: Request must be valid.
        serviceValidationUtilities.validateRequest(EventPublisherProxy.PublishEventRequest.class, publishEventRequest);

        // Start transmitter.
        startTransmitter();

        try
        {
            try
            {
                Object eventPayload = publishEventRequest.getEvent();

                // Transform payload (if required).
                PayloadTransformer transmitterPayloadTransformer = outboundMessageTransmitterConfiguration
                    .getTransmitterPayloadTransformer();
                if (transmitterPayloadTransformer != null)
                {
                    logger.trace("Transforming event payload.");
                    try
                    {
                        eventPayload = transmitterPayloadTransformer.transformPayload(new PayloadTransformer
                            .TransformPayloadRequest(eventPayload)).getPayload();
                    }
                    catch (PayloadTransformer.PayloadMustBeAmenableToTransformationException e)
                    {
                        // System error.
                        throw new RuntimeException(e);
                    }
                }

                // Compress payload (if required).
                PayloadCompressor transmitterPayloadCompressor = outboundMessageTransmitterConfiguration
                    .getTransmitterPayloadCompressor();
                if (transmitterPayloadCompressor != null)
                {
                    logger.trace("Compressing event payload.");

                    eventPayload = transmitterPayloadCompressor.compressPayload(new PayloadCompressor
                        .CompressPayloadRequest(eventPayload)).getCompressedPayload();
                }

                // Construct the JMS event message.
                Message eventMessage;
                try
                {
                    logger.trace("Constructing event message.");
                    eventMessage = outboundMessageTransmitterConfiguration.getMessageConstructor().constructMessage
                        (new MessageConstructor.ConstructMessageRequest(transmitterSession, eventPayload)).getMessage();
                }
                catch (RequestNotValidException | MessageConstructor.PayloadTypeNotSupportedException e)
                {
                    // System error.
                    throw new RuntimeException(e);
                }

                // Apply event type JMS message property if required.
                if (outboundMessageTransmitterConfiguration.getApplyEventTypeProperty())
                {
                    logger.trace("Applying event type '" + publishEventRequest.getEvent().getClass()
                        .getName() + "'.");
                    eventMessage.setStringProperty("eventType", publishEventRequest.getEvent().getClass()
                        .getName());
                }

                // Set any user-defined JMS message properties (if required).
                for (PropertySetter propertySetter : outboundMessageTransmitterConfiguration.getPropertySetters())
                {
                    logger.trace("Applying message property setter '" + propertySetter.getClass().getName() + "'.");
                    try
                    {
                        PropertySetterEventContext propertySetterEventContext = new PropertySetterEventContext
                            (publishEventRequest.getEvent());

                        eventMessage = propertySetter.setProperty(new PropertySetter.SetPropertyRequest
                            (propertySetterEventContext, eventMessage)).getMessage();

                        logger.trace("Message property setter '" + propertySetter.getClass().getName() + "' applied.");
                    }
                    catch (RequestNotValidException e)
                    {
                        // System error.
                        throw new RuntimeException(e);
                    }
                }

                logger.trace("Transmitting event message.");

                // Transmit the event message.
                try
                {
                    transmitterMessageProducer.send(eventMessage);

                }
                catch (JMSException e)
                {
                    // System error.
                    throw new RuntimeException("Unable to transmit the event message.", e);
                }
            }
            catch(Throwable t)
            {
                logger.error(t.getMessage(), t);

                throw t;
            }

            // Construct service response.
            return new EventPublisherProxy.PublishEventResponse();

        }
        finally
        {
            // Stop transmitter.
            stopTransmitter();
        }
    }

    private void startTransmitter()
    {
        logger.trace("Starting transmitter.");

        if (transmitterStarted)
        {
            // System error.
            throw new RuntimeException("Transmitter is already started.");
        }

        if (outboundMessageTransmitterConfiguration == null)
        {
            // System error.
            throw new RuntimeException("Transmitter has not been configured.");
        }

        try
        {
            // Create connection and session.

            logger.trace("Attempting to create transmitter connection and session.");

            createTransmitterConnection();
            createTransmitterSession();

            logger.trace("Transmitter connection and session created.");

            logger.trace("Attempting to startTransmitter receiver and transmitter connections and sessions.");

            // Start connection.
            startTransmitterConnection();

            logger.trace("Transmitter connection and session started.");

            // Create message producer.

            logger.trace("Attempting to create message producer.");

            // Create a message producer that is responsible for the transmission of responses or throwables.
            createTransmitterMessageProducer();

            logger.trace("Message producer created.");

            transmitterStarted = true;
        }
        catch (Exception e)
        {
            try
            {
                stopTransmitter();
            }
            catch(RuntimeException e1)
            {
                // Ignore. Allow original exception to be thrown.
            }

            throw e;
        }
    }

    private void stopTransmitter()
    {
        logger.trace("Stopping transmitter.");

        if (!transmitterStarted)
        {
            // System error.
            throw new RuntimeException("Transmitter is not started.");
        }

        // Close message receiver and producer.

        logger.trace("Attempting to close message producer.");

        closeTransmitterMessageProducer();

        logger.trace("Message producer closed.");

        // Close connections and sessions.

        logger.trace("Attempting to close transmitter connection and session.");

        closeTransmitterSession();
        closeTransmitterConnection();

        logger.trace("Transmitter connection and session closed.");

        transmitterStarted = false;
    }

    private String generateMessageTransmitterName()
    {
        logger.trace("Generating message transmitter name.");
        return UUID.randomUUID().toString();
    }

    private void createTransmitterConnection()
    {
        try
        {
            logger.trace("Creating transmitter connection.");
            transmitterConnection = outboundMessageTransmitterConfiguration.getTransmitterConnectionFactory()
                .createConnection();

            if (outboundMessageTransmitterConfiguration.getSetTransmitterConnectionClientId())
            {
                transmitterConnection.setClientID(outboundMessageTransmitterConfiguration.getMessageTransmitterName() +
                    "_Transmitter");

                logger.trace("Transmitter connection client ID = '" + transmitterConnection.getClientID() + "'");
            }
            else
            {
                logger.trace("Transmitter connection client ID not set.");
            }
        }
        catch (JMSException e)
        {
            // System error.
            throw new RuntimeException("Unable to create JMS connection for transmitter.", e);
        }
    }

    private void startTransmitterConnection()
    {
        try
        {
            logger.trace("Starting transmitter connection.");
            transmitterConnection.start();
        }
        catch (JMSException e)
        {
            // System error.
            throw new RuntimeException("Unable to startMonitoring JMS connection for transmitter.", e);
        }
    }

    private void closeTransmitterConnection()
    {
        try
        {
            logger.trace("Closing transmitter connection.");
            transmitterConnection.close();
        }
        catch (JMSException e)
        {
            // System error.
            throw new RuntimeException("Unable to close JMS connection for transmitter.", e);
        }
    }

    private void createTransmitterSession()
    {
        try
        {
            logger.trace("Creating transmitter session.");
            transmitterSession = transmitterConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }
        catch (JMSException e)
        {
            // System error.
            throw new RuntimeException("Unable to create JMS session for transmitter.", e);
        }
    }

    private void closeTransmitterSession()
    {
        try
        {
            logger.trace("Closing transmitter session.");
            transmitterSession.close();
        }
        catch (JMSException e)
        {
            // System error.
            throw new RuntimeException("Unable to close JMS session for transmitter.", e);
        }
    }

    /** Create a message producer that is responsible for the transmission of messages. */
    private void createTransmitterMessageProducer()
    {
        try
        {
            logger.trace("Creating transmitter message producer.");
            transmitterMessageProducer = transmitterSession.createProducer(outboundMessageTransmitterConfiguration
                .getTransmitterTopic());
        }
        catch (JMSException e)
        {
            // System error.
            throw new RuntimeException("Unable to create JMS message producer for transmitter.", e);
        }
    }

    /** TODO: Javadoc */
    private void closeTransmitterMessageProducer()
    {
        try
        {
            logger.trace("Closing transmitter message producer.");
            transmitterMessageProducer.close();
        }
        catch (JMSException e)
        {
            // System error.
            throw new RuntimeException("Unable to close JMS message producer for transmitter.", e);
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
            logger.trace(outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg);
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
            logger.trace(outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg, t);
        }

        @Override
        public boolean isTraceEnabled(Marker marker)
        {
            return logger.isTraceEnabled(marker);
        }

        @Override
        public void trace(Marker marker, String msg)
        {
            logger.trace(marker, outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg);
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
            logger.trace(marker, outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg, t);
        }

        @Override
        public boolean isDebugEnabled()
        {
            return logger.isDebugEnabled();
        }

        @Override
        public void debug(String msg)
        {
            logger.trace(outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg);
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
            logger.trace(outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg, t);
        }

        @Override
        public boolean isDebugEnabled(Marker marker)
        {
            return logger.isDebugEnabled(marker);
        }

        @Override
        public void debug(Marker marker, String msg)
        {
            logger.trace(marker, outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg);
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
            logger.trace(marker, outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg, t);
        }

        @Override
        public boolean isInfoEnabled()
        {
            return logger.isInfoEnabled();
        }

        @Override
        public void info(String msg)
        {
            logger.info(outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg);
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
            logger.info(outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg, t);
        }

        @Override
        public boolean isInfoEnabled(Marker marker)
        {
            return logger.isInfoEnabled(marker);
        }

        @Override
        public void info(Marker marker, String msg)
        {
            logger.info(marker, outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg);
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
            logger.info(marker, outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg, t);
        }

        @Override
        public boolean isWarnEnabled()
        {
            return logger.isWarnEnabled();
        }

        @Override
        public void warn(String msg)
        {
            logger.warn(outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg);
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
            logger.warn(outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg, t);
        }

        @Override
        public boolean isWarnEnabled(Marker marker)
        {
            return logger.isWarnEnabled(marker);
        }

        @Override
        public void warn(Marker marker, String msg)
        {
            logger.warn(marker, outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg);
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
            logger.warn(marker, outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg, t);
        }

        @Override
        public boolean isErrorEnabled()
        {
            return logger.isErrorEnabled();
        }

        @Override
        public void error(String msg)
        {
            logger.error(outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg);
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
            logger.error(outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg, t);
        }

        @Override
        public boolean isErrorEnabled(Marker marker)
        {
            return logger.isErrorEnabled(marker);
        }

        @Override
        public void error(Marker marker, String msg)
        {
            logger.error(marker, outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg);
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
            logger.error(marker, outboundMessageTransmitterConfiguration.getMessageTransmitterName() + ": " + msg, t);
        }

        @Override
        public String getName()
        {
            return logger.getName();
        }

        private Logger logger;
    }
    
    private LoggerWrapper logger;
    private static final Logger unwrappedLogger = LoggerFactory.getLogger(OutboundMessageTransmitterBean.class);
    private ServiceValidationUtilities serviceValidationUtilities;
    private Connection transmitterConnection;
    private Session transmitterSession;
    private MessageProducer transmitterMessageProducer;
    // Configuration must be explicitly set. Required instance for usage scenario not easy to qualify.
    @NotNull(message = "An outbound message transmitter configuration must be specified.")
    private OutboundMessageTransmitterConfiguration outboundMessageTransmitterConfiguration;
    private Boolean transmitterStarted = false;

}