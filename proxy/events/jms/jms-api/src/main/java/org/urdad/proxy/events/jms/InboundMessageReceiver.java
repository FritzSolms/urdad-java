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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.urdad.services.PreconditionViolation;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;

/**
 * TODO: Javadoc
 */
public interface InboundMessageReceiver
{

    /** TODO: Javadoc. */
    ConfigureResponse configure(ConfigureRequest configureRequest) throws RequestNotValidException;

    class ConfigureRequest extends Request
    {

        /** Default constructor. */
        public ConfigureRequest(){}

        /** Convenience constructor. */
        public ConfigureRequest(InboundMessageReceiverConfiguration inboundMessageReceiverConfiguration)
        {
            this.inboundMessageReceiverConfiguration = inboundMessageReceiverConfiguration;
        }

        /** TODO: Javadoc */
        public InboundMessageReceiverConfiguration getInboundMessageReceiverConfiguration()
        {
            return inboundMessageReceiverConfiguration;
        }

        public void setInboundMessageReceiverConfiguration(InboundMessageReceiverConfiguration inboundMessageReceiverConfiguration)
        {
            this.inboundMessageReceiverConfiguration = inboundMessageReceiverConfiguration;
        }

        @NotNull(message= "An inbound message transceiver configuration must be specified.")
        @Valid
        private InboundMessageReceiverConfiguration inboundMessageReceiverConfiguration;

    }

    class ConfigureResponse extends Response{}

    /** TODO: Javadoc. */
    StartMonitoringResponse startMonitoring(StartMonitoringRequest startMonitoringRequest) throws RequestNotValidException,
        ReceiverIsMonitoringException;

    class StartMonitoringRequest extends Request{}

    class StartMonitoringResponse extends Response{}

    /** TODO: Javadoc. */
    StopMonitoringResponse stopMonitoring(StopMonitoringRequest stopMonitoringRequest) throws RequestNotValidException,
        ReceiverIsNotMonitoringException;

    class StopMonitoringRequest extends Request{}

    class StopMonitoringResponse extends Response{}

    /** TODO: Javadoc. */
    CheckMonitoringResponse checkMonitoring(CheckMonitoringRequest checkMonitoringRequest) throws RequestNotValidException;

    class CheckMonitoringRequest extends Request{}

    class CheckMonitoringResponse extends Response
    {

        /** Default constructor. */
        public CheckMonitoringResponse(){}

        /** Convenience constructor. */
        public CheckMonitoringResponse(Boolean monitoring)
        {
            this.monitoring = monitoring;
        }

        /** TODO: Javadoc */
        public Boolean getMonitoring()
        {
            return monitoring;
        }

        public void setMonitoring(Boolean monitoring)
        {
            this.monitoring = monitoring;
        }

        private Boolean monitoring;
    }

    /** Thrown to indicate that the 'receiver is monitoring' pre-condition has been violated. */
    class ReceiverIsMonitoringException extends PreconditionViolation
    {

        /**
         * Constructs a new <code>ReceiverIsMonitoringException</code> exception with the specified detail
         * message.
         *
         * @param message the detail message.
         */
        public ReceiverIsMonitoringException(String message)
        {
            super(message);
        }

        /**
         * Constructs a new <code>ReceiverIsMonitoringException</code> exception with the specified detail
         * message and cause.
         *
         * @param message the detail message.
         * @param cause the cause.
         */
        public ReceiverIsMonitoringException(String message, Throwable cause)
        {
            super(message, cause);
        }

        /**
         * Constructs a new <code>ReceiverIsMonitoringException</code> exception with the specified cause.
         *
         * @param cause the cause.
         */
        public ReceiverIsMonitoringException(Throwable cause)
        {
            super(cause);
        }

    }

    /** Thrown to indicate that the 'receiver is not monitoring' pre-condition has been violated. */
    class ReceiverIsNotMonitoringException extends PreconditionViolation
    {

        /**
         * Constructs a new <code>ReceiverIsNotMonitoringException</code> exception with the specified detail message.
         *
         * @param message the detail message.
         */
        public ReceiverIsNotMonitoringException(String message)
        {
            super(message);
        }

        /**
         * Constructs a new <code>ReceiverIsNotMonitoringException</code> exception with the specified detail
         * message and cause.
         *
         * @param message the detail message.
         * @param cause the cause.
         */
        public ReceiverIsNotMonitoringException(String message, Throwable cause)
        {
            super(message, cause);
        }

        /**
         * Constructs a new <code>ReceiverIsNotMonitoringException</code> exception with the specified cause.
         *
         * @param cause the cause.
         */
        public ReceiverIsNotMonitoringException(Throwable cause)
        {
            super(cause);
        }

    }

    interface InboundMessageReceiverLocal extends InboundMessageReceiver{}
    interface InboundMessageReceiverRemote extends InboundMessageReceiver{}

}