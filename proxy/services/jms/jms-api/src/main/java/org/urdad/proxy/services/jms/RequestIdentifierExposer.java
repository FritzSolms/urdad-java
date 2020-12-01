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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;

/**
 * TODO: Javadoc
 */
public interface RequestIdentifierExposer
{

    /** Expore the request identifier assigned to a given request. */
    ExposeRequestIdentifierResponse exposeRequestIdentifier(ExposeRequestIdentifierRequest exposeRequestIdentifierRequest)
        throws RequestNotValidException;

    /** TODO: Javadoc */
    class ExposeRequestIdentifierRequest extends Request
    {

        /** Default constructor. */
        public ExposeRequestIdentifierRequest(){}

        /** Convenience constructor. */
        public ExposeRequestIdentifierRequest(String requestIdentifier, Request request)
        {
            this.requestIdentifier = requestIdentifier;
            this.request = request;
        }

        /** A unique identifier used to identify each service invocation. */
        public String getRequestIdentifier()
        {
            return requestIdentifier;
        }

        public void setRequestIdentifier(String requestIdentifier)
        {
            this.requestIdentifier = requestIdentifier;
        }

        /** TODO: Javadoc */
        public Request getRequest()
        {
            return request;
        }

        public void setRequest(Request request)
        {
            this.request = request;
        }

        @NotEmpty(message="A request identifier must be specified.")
        private String requestIdentifier;
        @NotNull(message= "A request must be specified.")
        private Request request;

    }

    /** TODO: Javadoc */
    class ExposeRequestIdentifierResponse extends Response{}

    /** TODO: Javadoc. */
    interface RequestIdentifierExposerLocal extends RequestIdentifierExposer{}
    /** TODO: Javadoc. */
    interface RequestIdentifierExposerRemote extends RequestIdentifierExposer{}

}