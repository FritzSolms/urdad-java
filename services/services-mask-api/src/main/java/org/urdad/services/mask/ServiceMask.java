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

package org.urdad.services.mask;

import org.urdad.services.Request;
import org.urdad.services.Response;

import javax.validation.constraints.NotNull;
import java.util.concurrent.Future;

/**
 * TODO: Javadoc
 */
public interface ServiceMask
{

    /** Invokes the underlying/wrapped service with the specified request. */
    Response invokeService(Request request) throws Throwable;

    Future<? extends Response> invokeServiceAsync(Request request) throws Throwable;


    /** Retrieves the underlying service provider type that is associated with this proxy. */
    RetrieveServiceProviderTypeResponse retrieveServiceProviderType(RetrieveServiceProviderTypeRequest retrieveServiceProviderTypeRequest);

    class RetrieveServiceProviderTypeRequest extends Request{}

    class RetrieveServiceProviderTypeResponse extends Response
    {
        /** Default constructor. */
        public RetrieveServiceProviderTypeResponse(){}

        /** Convenience constructor. */
        public RetrieveServiceProviderTypeResponse(Class serviceProviderType)
        {
            this.serviceProviderType = serviceProviderType;
        }

        /** TODO: Javadoc */
        public Class getServiceProviderType()
        {
            return serviceProviderType;
        }

        public void setServiceProviderType(Class serviceProviderType)
        {
            this.serviceProviderType = serviceProviderType;
        }

        @NotNull(message= "A service provider type must be specified.")
        private Class serviceProviderType;

    }

    /** Thrown to indicate that a response has not been received before timeout. */
    class ResponseNotReceivedBeforeTimeoutError extends RuntimeException
    {

        /**
         * Constructs a new <code>ResponseNotReceivedBeforeTimeoutError</code> exception with the specified detail
         * message.
         *
         * @param message the detail message.
         */
        public ResponseNotReceivedBeforeTimeoutError(String message)
        {
            super(message);
        }

        /**
         * Constructs a new <code>ResponseNotReceivedBeforeTimeoutError</code> exception with the specified detail
         * message and cause.
         *
         * @param message the detail message.
         * @param cause the cause.
         */
        public ResponseNotReceivedBeforeTimeoutError(String message, Throwable cause)
        {
            super(message, cause);
        }

        /**
         * Constructs a new <code>ResponseNotReceivedBeforeTimeoutError</code> exception with the specified cause.
         *
         * @param cause the cause.
         */
        public ResponseNotReceivedBeforeTimeoutError(Throwable cause)
        {
            super(cause);
        }

    }

    interface ServiceMaskLocal extends ServiceMask {}
    interface ServiceMaskRemote extends ServiceMask {}

}