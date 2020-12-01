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

import java.util.UUID;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.urdad.events.Event;
import org.urdad.events.EventPublisher;
import org.urdad.events.services.ServiceEvent;
import org.urdad.events.services.ServiceProviderEvent;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.mask.ServiceMask;
import org.urdad.validation.services.ServiceValidationUtilities;

/** Abstract implementation of a message transceiver. */
public abstract class MessageTransceiverBean
{

    protected String generateMessageTransceiverName()
    {
        logger.trace("Generating message transceiver name.");
        return UUID.randomUUID().toString();
    }

    protected void rest(Long timeout)
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

    protected void createReceiverConnection()
    {
        try
        {
            logger.trace("Creating receiver connection.");
            receiverConnection = messageTransceiverConfiguration.getReceiverConnectionFactory().createConnection();

            if (messageTransceiverConfiguration.getSetReceiverConnectionClientId())
            {
                receiverConnection.setClientID(messageTransceiverConfiguration.getMessageTransceiverName() + "_Receiver");

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

    protected void startReceiverConnection()
    {
        try
        {
            logger.trace("Starting receiver connection.");
            receiverConnection.start();
        }
        catch (JMSException e)
        {
            // System error.
            throw new RuntimeException("Unable to startMonitoring JMS connection for receiver.", e);
        }
    }

    protected void closeReceiverConnection()
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

    protected void createReceiverSession()
    {
        try
        {
            if (messageTransceiverConfiguration.getReceiverTransacted())
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

    protected void commitReceiverSessionIfTransacted()
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

    protected void rollbackReceiverSessionIfTransacted()
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

    protected void closeReceiverSession()
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

    protected void createTransmitterConnection()
    {
        try
        {
            logger.trace("Creating transmitter connection.");
            transmitterConnection = messageTransceiverConfiguration.getTransmitterConnectionFactory().createConnection();

            if (messageTransceiverConfiguration.getSetTransmitterConnectionClientId())
            {
                transmitterConnection.setClientID(messageTransceiverConfiguration.getMessageTransceiverName() +
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

    protected void startTransmitterConnection()
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

    protected void closeTransmitterConnection()
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

    protected void createTransmitterSession()
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

    protected void closeTransmitterSession()
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

    /** Create a message consumer that is responsible for the receipt of messages. */
    protected void createReceiverMessageConsumer()
    {
        try
        {
            logger.trace("Creating receiver message consumer.");
            receiverMessageConsumer = receiverSession.createConsumer(messageTransceiverConfiguration
                .getReceiverDestination());
        }
        catch (JMSException e)
        {
            // System error.
            throw new RuntimeException("Unable to create JMS message consumer for receiver.", e);
        }
    }

    /** Create a message consumer that is responsible for the receipt of messages. */
    protected void createReceiverMessageConsumer(String messageSelector)
    {
        try
        {
            logger.trace("Creating receiver message consumer with message selector '" + messageSelector + "'.");
            receiverMessageConsumer = receiverSession.createConsumer(messageTransceiverConfiguration.getReceiverDestination(),
                messageSelector);
        }
        catch (JMSException e)
        {
            // System error.
            throw new RuntimeException("Unable to create JMS message consumer for receiver.", e);
        }
    }

    /** TODO: Javadoc */
    protected void closeReceiverMessageConsumer()
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

    /** Create a message producer that is responsible for the transmission of messages. */
    protected void createTransmitterMessageProducer()
    {
        try
        {
            logger.trace("Creating transmitter message producer.");
            transmitterMessageProducer = transmitterSession.createProducer(messageTransceiverConfiguration
                .getTransmitterDestination());

            // Ensure that unconsumed messages do not exist indefinitely.
            if ((messageTransceiverConfiguration.getReceiverMessageConsumptionTimeout() != null) && (messageTransceiverConfiguration
                .getReceiverMessageConsumptionTimeout() > 0))
            {
                transmitterMessageProducer.setTimeToLive(messageTransceiverConfiguration.getReceiverMessageConsumptionTimeout());
            }
        }
        catch (JMSException e)
        {
            // System error.
            throw new RuntimeException("Unable to create JMS message producer for transmitter.", e);
        }
    }

    /** TODO: Javadoc */
    protected void closeTransmitterMessageProducer()
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

    /** TODO: Javadoc */
    protected void publishEvent(Event event)
    {
    	logger.trace("Firing Event: {}",event);
        if (messageTransceiverConfiguration.getEventPublisher() != null)
        {
            EventPublisher eventPublisher = messageTransceiverConfiguration.getEventPublisher();

            try
            {
                eventPublisher.publishEventAsync(new EventPublisher.PublishEventRequest(event));
            }
            catch (RequestNotValidException | EventPublisher.EventMustBeSupportedException e)
            {
                // System error.
                throw new RuntimeException("Unable to publish event.", e);
            }
        }

    }

    protected class LoggerWrapper implements org.slf4j.Logger
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
            logger.trace(messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg);
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
            logger.trace(messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg, t);
        }

        @Override
        public boolean isTraceEnabled(Marker marker)
        {
            return logger.isTraceEnabled(marker);
        }

        @Override
        public void trace(Marker marker, String msg)
        {
            logger.trace(marker, messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg);
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
            logger.trace(marker, messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg, t);
        }

        @Override
        public boolean isDebugEnabled()
        {
            return logger.isDebugEnabled();
        }

        @Override
        public void debug(String msg)
        {
            logger.trace(messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg);
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
            logger.trace(messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg, t);
        }

        @Override
        public boolean isDebugEnabled(Marker marker)
        {
            return logger.isDebugEnabled(marker);
        }

        @Override
        public void debug(Marker marker, String msg)
        {
            logger.trace(marker, messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg);
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
            logger.trace(marker, messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg, t);
        }

        @Override
        public boolean isInfoEnabled()
        {
            return logger.isInfoEnabled();
        }

        @Override
        public void info(String msg)
        {
            logger.info(messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg);
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
            logger.info(messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg, t);
        }

        @Override
        public boolean isInfoEnabled(Marker marker)
        {
            return logger.isInfoEnabled(marker);
        }

        @Override
        public void info(Marker marker, String msg)
        {
            logger.info(marker, messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg);
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
            logger.info(marker, messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg, t);
        }

        @Override
        public boolean isWarnEnabled()
        {
            return logger.isWarnEnabled();
        }

        @Override
        public void warn(String msg)
        {
            logger.warn(messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg);
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
            logger.warn(messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg, t);
        }

        @Override
        public boolean isWarnEnabled(Marker marker)
        {
            return logger.isWarnEnabled(marker);
        }

        @Override
        public void warn(Marker marker, String msg)
        {
            logger.warn(marker, messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg);
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
            logger.warn(marker, messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg, t);
        }

        @Override
        public boolean isErrorEnabled()
        {
            return logger.isErrorEnabled();
        }

        @Override
        public void error(String msg)
        {
            logger.error(messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg);
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
            logger.error(messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg, t);
        }

        @Override
        public boolean isErrorEnabled(Marker marker)
        {
            return logger.isErrorEnabled(marker);
        }

        @Override
        public void error(Marker marker, String msg)
        {
            logger.error(marker, messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg);
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
            logger.error(marker, messageTransceiverConfiguration.getMessageTransceiverName() + ": " + msg, t);
        }

        @Override
        public String getName()
        {
            return logger.getName();
        }

        private Logger logger;
    }

    /** TODO: Javadoc */
    protected <T extends ServiceProviderEvent>  T constructServiceProviderEvent(Class<T> serviceProviderEventType,
        ServiceMask serviceMask)
    {
        try
        {
            ServiceProviderEvent serviceProviderEvent = serviceProviderEventType.newInstance();
            serviceProviderEvent.setLocation(messageTransceiverConfiguration.getMessageTransceiverLocation());
            serviceProviderEvent.setServiceProviderType(inferServiceProviderType(serviceMask));
            serviceProviderEvent.setServiceProviderIdentifier(messageTransceiverConfiguration
                .getServiceProviderIdentifier());

            return (T) serviceProviderEvent;
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            // System error.
            throw new RuntimeException("Unable to construct service provider event.", e);
        }
    }

    /** TODO: Javadoc */
    protected <T extends ServiceEvent>  T constructServiceEvent(Class<T> serviceEventType, Request request)
    {
        try
        {
            ServiceEvent serviceEvent = serviceEventType.newInstance();
            serviceEvent.setLocation(messageTransceiverConfiguration.getMessageTransceiverLocation());
            serviceEvent.setServiceProviderType(inferServiceProviderType(request));
            serviceEvent.setService(inferService(request));
            serviceEvent.setServiceProviderIdentifier(messageTransceiverConfiguration
                .getServiceProviderIdentifier());

            return (T) serviceEvent;
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            // System error.
            throw new RuntimeException("Unable to construct service event.", e);
        }
    }

    private String inferServiceProviderType(ServiceMask serviceMask)
    {
        return serviceMask.retrieveServiceProviderType(new ServiceMask
            .RetrieveServiceProviderTypeRequest()).getServiceProviderType().getName();
    }

    private String inferServiceProviderType(Request request)
    {
        return request.getClass().getEnclosingClass().getName();
    }

    private String inferService(Request request)
    {
        // Find the request's simple name, remove the 'Request' string.
        String service = request.getClass().getSimpleName().replace("Request", "");
        // Make first letter lowercase.
        service = service.substring(0, 1).toLowerCase() + service.substring(1);
        return service;
    }

    protected LoggerWrapper logger;
    protected Class serviceProviderType;
    protected ServiceValidationUtilities serviceValidationUtilities;
    protected MessageTransceiverConfiguration messageTransceiverConfiguration;
    protected Connection receiverConnection;
    protected Session receiverSession;
    protected Connection transmitterConnection;
    protected Session transmitterSession;
    protected MessageConsumer receiverMessageConsumer;
    protected MessageProducer transmitterMessageProducer;
    protected EventPublisher eventPublisher;

}