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

import javax.jms.Message;
import javax.validation.constraints.NotNull;
import org.urdad.services.PreconditionViolation;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;

/**
 * TODO: Javadoc
 */
public interface PropertySetter
{

    /** Set a particular property on a JMS message. */
    SetPropertyResponse setProperty(SetPropertyRequest setPropertyRequest) throws RequestNotValidException,
        OriginalRequestNotSupportedException;

    /** TODO: Javadoc */
    class SetPropertyRequest extends Request
    {

        /** Default constructor. */
        public SetPropertyRequest(){}

        /** Convenience constructor. */
        public SetPropertyRequest(PropertySetterContext propertySetterContext, Message message)
        {
            this.propertySetterContext = propertySetterContext;
            this.message = message;
        }

        public PropertySetterContext getPropertySetterContext()
        {
            return propertySetterContext;
        }

        public void setPropertySetterContext(PropertySetterContext propertySetterContext)
        {
            this.propertySetterContext = propertySetterContext;
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

        private PropertySetterContext propertySetterContext;
        @NotNull(message = "A message must be specified.")
        private Message message;
    }

    /** TODO: Javadoc */
    class SetPropertyResponse extends Response
    {
        /** Default constructor. */
        public SetPropertyResponse(){}

        /** Convenience constructor. */
        public SetPropertyResponse(Message message)
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

    /** Thrown to indicate that the 'original request must be supported' pre-condition has been violated. */
    class OriginalRequestNotSupportedException extends PreconditionViolation
    {

        /**
         * Constructs a new <code>PayloadTypeNotSupportedException</code> exception with the specified detail
         * message.
         *
         * @param message the detail message.
         */
        public OriginalRequestNotSupportedException(String message)
        {
            super(message);
        }

        /**
         * Constructs a new <code>OriginalRequestNotSupportedException</code> exception with the specified detail
         * message and cause.
         *
         * @param message the detail message.
         * @param cause the cause.
         */
        public OriginalRequestNotSupportedException(String message, Throwable cause)
        {
            super(message, cause);
        }

        /**
         * Constructs a new <code>OriginalRequestNotSupportedException</code> exception with the specified cause.
         *
         * @param cause the cause.
         */
        public OriginalRequestNotSupportedException(Throwable cause)
        {
            super(cause);
        }

    }

    /** TODO: Javadoc. */
    interface PropertySetterLocal extends PropertySetter{}

    /** TODO: Javadoc. */
    interface PropertySetterRemote extends PropertySetter{}

}