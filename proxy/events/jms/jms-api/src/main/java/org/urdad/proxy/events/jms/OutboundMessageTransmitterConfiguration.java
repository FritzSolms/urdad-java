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

import java.util.HashSet;
import java.util.Set;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.validation.constraints.NotNull;
import org.urdad.proxy.jms.MessageConstructor;
import org.urdad.proxy.jms.PayloadCompressor;
import org.urdad.proxy.jms.PayloadTransformer;
import org.urdad.proxy.jms.PropertySetter;

/**
 * TODO: Javadoc
 */
@ValidOutboundMessageTransmitterConfiguration
public class OutboundMessageTransmitterConfiguration
{

    /** TODO: Javadoc */
    public String getMessageTransmitterName()
    {
        return messageTransmitterName;
    }

    public void setMessageTransmitterName(String messageTransmitterName)
    {
        this.messageTransmitterName = messageTransmitterName;
    }

    /* Transmitter configuration */

    /** TODO: Javadoc */
    public ConnectionFactory getTransmitterConnectionFactory()
    {
        return transmitterConnectionFactory;
    }

    public void setTransmitterConnectionFactory(ConnectionFactory transmitterConnectionFactory)
    {
        this.transmitterConnectionFactory = transmitterConnectionFactory;
    }

    /** TODO: Javadoc */
    public Boolean getSetTransmitterConnectionClientId()
    {
        return setTransmitterConnectionClientId;
    }

    public void setSetTransmitterConnectionClientId(Boolean setTransmitterConnectionClientId)
    {
        this.setTransmitterConnectionClientId = setTransmitterConnectionClientId;
    }

    /** TODO: Javadoc */
    public Topic getTransmitterTopic()
    {
        return transmitterTopic;
    }

    public void setTransmitterTopic(Topic transmitterTopic)
    {
        this.transmitterTopic = transmitterTopic;
    }

    /** TODO: Javadoc */
    public PayloadTransformer getTransmitterPayloadTransformer()
    {
        return transmitterPayloadTransformer;
    }

    public void setTransmitterPayloadTransformer(PayloadTransformer transmitterPayloadTransformer)
    {
        this.transmitterPayloadTransformer = transmitterPayloadTransformer;
    }

    /** TODO: Javadoc */
    public PayloadCompressor getTransmitterPayloadCompressor()
    {
        return transmitterPayloadCompressor;
    }

    public void setTransmitterPayloadCompressor(PayloadCompressor transmitterPayloadCompressor)
    {
        this.transmitterPayloadCompressor = transmitterPayloadCompressor;
    }

    /** TODO: Javadoc */
    public MessageConstructor getMessageConstructor()
    {
        return messageConstructor;
    }

    public void setMessageConstructor(MessageConstructor messageConstructor)
    {
        this.messageConstructor = messageConstructor;
    }

    /** TODO: Javadoc */
    public Boolean getApplyEventTypeProperty()
    {
        return applyEventTypeProperty;
    }

    public void setApplyEventTypeProperty(Boolean applyEventTypeProperty)
    {
        this.applyEventTypeProperty = applyEventTypeProperty;
    }

    /** TODO: Javadoc */
    public Set<PropertySetter> getPropertySetters()
    {
        return propertySetters;
    }

    public void setPropertySetters(Set<PropertySetter> propertySetters)
    {
        this.propertySetters = propertySetters;
    }

    private String messageTransmitterName;
    @NotNull(message = "A transmitter JMS connection factory must be specified.")
    private ConnectionFactory transmitterConnectionFactory;
    @NotNull(message = "The set transmitter connection client ID property may not be null")
    private Boolean setTransmitterConnectionClientId = true; // Default.
    @NotNull(message = "A transmitter JMS destination must be specified.")
    private Topic transmitterTopic;
    private PayloadTransformer transmitterPayloadTransformer;
    private PayloadCompressor transmitterPayloadCompressor;
    @NotNull(message = "A message constructor must be specified.")
    private MessageConstructor messageConstructor;
    @NotNull(message = "The apply event type property may not be null")
    private Boolean applyEventTypeProperty = true; // Default. (Value derived from event during invocation)
    @NotNull(message = "The set used for property setters may not be null.")
    private Set<PropertySetter> propertySetters = new HashSet<>();

}