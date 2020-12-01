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

import javax.validation.constraints.NotNull;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;

/**
 * TODO: Javadoc
 */
public interface MessageConsumptionGovernor
{

    /** Determines whether message consumption is permitted. */
    ConsumptionPermittedResponse consumptionPermitted(ConsumptionPermittedRequest consumptionPermittedRequest) throws
        RequestNotValidException;

    /** TODO: Javadoc */
    class ConsumptionPermittedRequest extends Request{}

    /** TODO: Javadoc */
    class ConsumptionPermittedResponse extends Response
    {
        /** Default constructor. */
        public ConsumptionPermittedResponse(){}

        /** Convenience constructor. */
        public ConsumptionPermittedResponse(Boolean permitted)
        {
            this.permitted = permitted;
        }

        /** TODO: Javadoc */
        public Boolean getPermitted()
        {
            return permitted;
        }

        public void setPermitted(Boolean permitted)
        {
            this.permitted = permitted;
        }

        @NotNull(message= "An indication of whether message consumption is permitted must be provided.")
        private Boolean permitted;

    }

    /** TODO: Javadoc. */
    interface MessageConsumptionGovernorLocal extends MessageConsumptionGovernor{}

    /** TODO: Javadoc. */
    interface MessageConsumptionGovernorRemote extends MessageConsumptionGovernor{}

}
