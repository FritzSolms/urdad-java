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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.urdad.services.mask.ServiceMask;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;

/**
 * TODO: Javadoc
 */
public interface OutboundMessageTransceiver extends ServiceMask
{

    /** TODO: Javadoc. */
    ConfigureResponse configure(ConfigureRequest configureRequest) throws RequestNotValidException;

    /** TODO: Javadoc */
    class ConfigureRequest extends Request
    {

        /** Default constructor. */
        public ConfigureRequest(){}

        /** Convenience constructor. */
        public ConfigureRequest(OutboundMessageTransceiverConfiguration outboundMessageTransceiverConfiguration)
        {
            this.outboundMessageTransceiverConfiguration = outboundMessageTransceiverConfiguration;
        }

        /** TODO: Javadoc */
        public OutboundMessageTransceiverConfiguration getOutboundMessageTransceiverConfiguration()
        {
            return outboundMessageTransceiverConfiguration;
        }

        public void setOutboundMessageTransceiverConfiguration(OutboundMessageTransceiverConfiguration
            outboundMessageTransceiverConfiguration)
        {
            this.outboundMessageTransceiverConfiguration = outboundMessageTransceiverConfiguration;
        }

        @NotNull(message= "An outbound message transceiver configuration must be specified.")
        @Valid
        private OutboundMessageTransceiverConfiguration outboundMessageTransceiverConfiguration;

    }

    /** TODO: Javadoc */
    class ConfigureResponse extends Response{}

    interface OutboundMessageTransceiverLocal extends OutboundMessageTransceiver{}
    interface OutboundMessageTransceiverRemote extends OutboundMessageTransceiver{}

}
