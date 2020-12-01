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

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Session;
import javax.validation.constraints.NotNull;
import org.urdad.services.PreconditionViolation;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;

/**
 * TODO: Javadoc
 */
public interface MessageConstructor
{

    /** Constructs a JMS message. */
    ConstructMessageResponse constructMessage(ConstructMessageRequest constructMessageRequest) throws
        RequestNotValidException, PayloadTypeNotSupportedException;

    /** TODO: Javadoc */
    class ConstructMessageRequest extends Request
    {

        /** Default constructor. */
        public ConstructMessageRequest(){}

        /** Convenience constructor. */
        public ConstructMessageRequest(Session session, Object payload)
        {
            this.session = session;
            this.payload = payload;
        }

        /** Convenience constructor. */
        public ConstructMessageRequest(Session session, String correlationId, Object payload)
        {
            this.session = session;
            this.correlationId = correlationId;
            this.payload = payload;
        }

        /** Convenience constructor. */
        public ConstructMessageRequest(Session session, Destination replyTo, Object payload)
        {
            this.session = session;
            this.replyTo = replyTo;
            this.payload = payload;
        }

        /** Convenience constructor. */
        public ConstructMessageRequest(Session session, String correlationId, Destination replyTo, Object payload)
        {
            this.session = session;
            this.correlationId = correlationId;
            this.replyTo = replyTo;
            this.payload = payload;
        }

        /** TODO: Javadoc */
        public Session getSession()
        {
            return session;
        }

        public void setSession(Session session)
        {
            this.session = session;
        }

        /** TODO: Javadoc */
        public String getCorrelationId()
        {
            return correlationId;
        }

        public void setCorrelationId(String correlationId)
        {
            this.correlationId = correlationId;
        }

        public Destination getReplyTo()
        {
            return replyTo;
        }

        public void setReplyTo(Destination replyTo)
        {
            this.replyTo = replyTo;
        }

        /** TODO: Javadoc */
        public Object getPayload()
        {
            return payload;
        }

        public void setPayload(Object payload)
        {
            this.payload = payload;
        }

        @NotNull(message = "A session must be specified.")
        private Session session;
        private String correlationId;
        private Destination replyTo;
        @NotNull(message= "A payload must be specified.")
        private Object payload;

    }

    /** TODO: Javadoc */
    class ConstructMessageResponse extends Response
    {
        /** Default constructor. */
        public ConstructMessageResponse(){}

        /** Convenience constructor. */
        public ConstructMessageResponse(Message message)
        {
            this.message = message;
        }

        /** TODO: Javadoc */
        public Message getMessage()
        {
            return message;
        }

        public void setMessage(Message message)
        {
            this.message = message;
        }

        @NotNull(message= "A message must be specified.")
        private Message message;

    }

    /**
     * Thrown to indicate that the 'payload type must be supported' pre-condition has been violated.
     */
    class PayloadTypeNotSupportedException extends PreconditionViolation
    {

        /**
         * Constructs a new <code>PayloadTypeNotSupportedException</code> exception with the specified detail
         * message.
         *
         * @param message the detail message.
         */
        public PayloadTypeNotSupportedException(String message)
        {
            super(message);
        }

        /**
         * Constructs a new <code>PayloadTypeNotSupportedException</code> exception with the specified detail
         * message and cause.
         *
         * @param message the detail message.
         * @param cause the cause.
         */
        public PayloadTypeNotSupportedException(String message, Throwable cause)
        {
            super(message, cause);
        }

        /**
         * Constructs a new <code>PayloadTypeNotSupportedException</code> exception with the specified cause.
         *
         * @param cause the cause.
         */
        public PayloadTypeNotSupportedException(Throwable cause)
        {
            super(cause);
        }

    }

    /** TODO: Javadoc. */
    interface MessageConstructorLocal extends MessageConstructor{}

    /** TODO: Javadoc. */
    interface MessageConstructorRemote extends MessageConstructor{}

}
