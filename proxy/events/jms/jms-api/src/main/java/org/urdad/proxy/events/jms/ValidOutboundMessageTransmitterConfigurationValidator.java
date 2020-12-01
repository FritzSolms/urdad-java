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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * A validator that confirms whether an outbound message transmitter configuration contents represent a valid
 * configuration.
 */
public class ValidOutboundMessageTransmitterConfigurationValidator implements ConstraintValidator
    <ValidOutboundMessageTransmitterConfiguration, OutboundMessageTransmitterConfiguration>
{

    /**
     * Initializes the validator in preparation for {@link #isValid(OutboundMessageTransmitterConfiguration,
     * ConstraintValidatorContext)} calls. The constraint annotation for a given constraint
     * declaration is passed.
     * <p>
     * This method is guaranteed to be called before any use of this instance for validation.
     *
     * @param validOutboundMessageReceiverConfiguration annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(ValidOutboundMessageTransmitterConfiguration validOutboundMessageReceiverConfiguration){}

    /**
     * Checks whether an outbound message transceiver configuration contents represent a valid configuration..
     *
     * @param outboundMessageTransmitterConfiguration outboundMessageTransceiverConfiguration to validate
     * @param context context in which the constraint is evaluated
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(OutboundMessageTransmitterConfiguration outboundMessageTransmitterConfiguration,
        ConstraintValidatorContext context)
    {
        return true; //TODO revisit if needed.
    }

}