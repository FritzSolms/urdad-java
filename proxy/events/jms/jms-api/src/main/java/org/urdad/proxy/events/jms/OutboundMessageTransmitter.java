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
import org.urdad.events.proxy.EventPublisherProxy;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;

/**
 * TODO: Javadoc
 */
public interface OutboundMessageTransmitter extends EventPublisherProxy
{

    /** TODO: Javadoc. */
    ConfigureResponse configure(ConfigureRequest configureRequest) throws RequestNotValidException;

    /** TODO: Javadoc */
    class ConfigureRequest extends Request
    {

        /** Default constructor. */
        public ConfigureRequest(){}

        /** Convenience constructor. */
        public ConfigureRequest(OutboundMessageTransmitterConfiguration outboundMessageTransmitterConfiguration)
        {
            this.outboundMessageTransmitterConfiguration = outboundMessageTransmitterConfiguration;
        }

        /** TODO: Javadoc */
        public OutboundMessageTransmitterConfiguration getOutboundMessageTransmitterConfiguration()
        {
            return outboundMessageTransmitterConfiguration;
        }

        public void setOutboundMessageTransmitterConfiguration(OutboundMessageTransmitterConfiguration
            outboundMessageTransmitterConfiguration)
        {
            this.outboundMessageTransmitterConfiguration = outboundMessageTransmitterConfiguration;
        }

        @NotNull(message= "An outbound message transmitter configuration must be specified.")
        @Valid
        private OutboundMessageTransmitterConfiguration outboundMessageTransmitterConfiguration;

    }

    /** TODO: Javadoc */
    class ConfigureResponse extends Response{}

    interface OutboundMessageTransmitterLocal extends OutboundMessageTransmitter{}
    interface OutboundMessageTransmitterRemote extends OutboundMessageTransmitter{}

}
