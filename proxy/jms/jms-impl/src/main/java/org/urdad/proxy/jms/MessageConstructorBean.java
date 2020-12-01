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

package org.urdad.proxy.jms;

import com.google.common.base.Strings;
import java.io.Serializable;
import javax.inject.Inject;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;

/**
 * TODO: Javadoc
 */
public class MessageConstructorBean implements MessageConstructor.MessageConstructorLocal, MessageConstructor
    .MessageConstructorRemote
{

    public MessageConstructorBean(ServiceValidationUtilities serviceValidationUtilities)
    {
        if (serviceValidationUtilities == null)
        {
            throw new RuntimeException("A service validation utilities must be specified.");
        }

        this.serviceValidationUtilities = serviceValidationUtilities;
    }

    @Override
    public ConstructMessageResponse constructMessage(ConstructMessageRequest constructMessageRequest)
        throws RequestNotValidException, PayloadTypeNotSupportedException
    {
        logger.trace("About to construct JMS message.");

        // Check pre-condition: Request must be valid.
        serviceValidationUtilities.validateRequest(ConstructMessageRequest.class, constructMessageRequest);

        Session session = constructMessageRequest.getSession();
        Object payload = constructMessageRequest.getPayload();

        Message message;

        try
        {
            if (payload instanceof String)
            {
                logger.trace("Payload is of type String.");
                message = session.createTextMessage((String) payload);
            }
            else if (payload instanceof byte[])
            {
                logger.trace("Payload is of type byte array.");
                message = session.createBytesMessage();
                ((BytesMessage) message).writeBytes((byte[]) payload);
            }
            else if (payload instanceof Serializable)
            {
                logger.trace("Payload is of type Serializable.");
                message = session.createObjectMessage((Serializable) payload);
            }
            else
            {
                // Check pre-condition: Payload type must be supported.
                throw new PayloadTypeNotSupportedException("Message payloads of type '" + payload.getClass().getName() +
                    "' are not supported by this message constructor.");
            }

            if (!Strings.isNullOrEmpty(constructMessageRequest.getCorrelationId()))
            {
                logger.trace("Setting correlation ID '" + constructMessageRequest.getCorrelationId() + "'.");

                message.setJMSCorrelationID(constructMessageRequest.getCorrelationId());
            }


            if (constructMessageRequest.getReplyTo() != null)
            {
                logger.trace("Setting reply to '" + constructMessageRequest.getReplyTo() + "'.");

                message.setJMSReplyTo(constructMessageRequest.getReplyTo());
            }
        }
        catch (JMSException e)
        {
            // System error.
            throw new RuntimeException(e);
        }

        logger.trace("Constructed message " + message);

        // Create service response.
        return new ConstructMessageResponse(message);
    }

    private static final Logger logger = LoggerFactory.getLogger(MessageConstructorBean.class);
    @Inject
    private ServiceValidationUtilities serviceValidationUtilities;

}