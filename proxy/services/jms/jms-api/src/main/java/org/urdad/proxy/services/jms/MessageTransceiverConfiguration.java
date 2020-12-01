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

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.urdad.events.EventPublisher;
import org.urdad.proxy.jms.MessageConstructor;
import org.urdad.proxy.jms.PayloadCompressor;
import org.urdad.proxy.jms.PayloadDecompressor;
import org.urdad.proxy.jms.PayloadTransformer;

/**
 * TODO: Javadoc
 */
public abstract class MessageTransceiverConfiguration
{

    /** TODO: Javadoc */
    public String getMessageTransceiverName()
    {
        return messageTransceiverName;
    }

    public void setMessageTransceiverName(String messageTransceiverName)
    {
        this.messageTransceiverName = messageTransceiverName;
    }

    /** TODO: Javadoc */
    public String getMessageTransceiverLocation()
    {
        return messageTransceiverLocation;
    }

    public void setMessageTransceiverLocation(String messageTransceiverLocation)
    {
        this.messageTransceiverLocation = messageTransceiverLocation;
    }

    /** TODO: Javadoc */
    public String getServiceProviderIdentifier()
    {
        return serviceProviderIdentifier;
    }

    public void setServiceProviderIdentifier(String serviceProviderIdentifier)
    {
        this.serviceProviderIdentifier = serviceProviderIdentifier;
    }

    /* Receiver configuration */

    /** TODO: Javadoc */
    public ConnectionFactory getReceiverConnectionFactory()
    {
        return receiverConnectionFactory;
    }

    public void setReceiverConnectionFactory(ConnectionFactory receiverConnectionFactory)
    {
        this.receiverConnectionFactory = receiverConnectionFactory;
    }

    /** TODO: Javadoc */
    public Boolean getSetReceiverConnectionClientId()
    {
        return setReceiverConnectionClientId;
    }

    public void setSetReceiverConnectionClientId(Boolean setReceiverConnectionClientId)
    {
        this.setReceiverConnectionClientId = setReceiverConnectionClientId;
    }

    /** TODO: Javadoc */
    public Destination getReceiverDestination()
    {
        return receiverDestination;
    }

    public void setReceiverDestination(Destination receiverDestination)
    {
        this.receiverDestination = receiverDestination;
    }

    /** TODO: Javadoc */
    public PayloadTransformer getReceiverPayloadTransformer()
    {
        return receiverPayloadTransformer;
    }

    public void setReceiverPayloadTransformer(PayloadTransformer receiverPayloadTransformer)
    {
        this.receiverPayloadTransformer = receiverPayloadTransformer;
    }

    /** TODO: Javadoc */
    public PayloadDecompressor getReceiverPayloadDecompressor()
    {
        return receiverPayloadDecompressor;
    }

    public void setReceiverPayloadDecompressor(PayloadDecompressor receiverPayloadDecompressor)
    {
        this.receiverPayloadDecompressor = receiverPayloadDecompressor;
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
    public Destination getTransmitterDestination()
    {
        return transmitterDestination;
    }

    public void setTransmitterDestination(Destination transmitterDestination)
    {
        this.transmitterDestination = transmitterDestination;
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

    /* Shared Receiver * Transmitter configuration. (Convenience methods) */

    public void setReceiverAndTransmitterConnectionFactory(ConnectionFactory receiverAndTransmitterConnectionFactory)
    {
        setReceiverConnectionFactory(receiverAndTransmitterConnectionFactory);
        setTransmitterConnectionFactory(receiverAndTransmitterConnectionFactory);
    }

    public void setReceiverAndTransmitterDestination(Destination receiverAndTransmitterDestination)
    {
        setReceiverDestination(receiverAndTransmitterDestination);
        setTransmitterDestination(receiverAndTransmitterDestination);
    }

    /** TODO: Javadoc */
    public Boolean getReceiverTransacted()
    {
        return receiverTransacted;
    }

    public void setReceiverTransacted(Boolean receiverTransacted)
    {
        this.receiverTransacted = receiverTransacted;
    }

    /** TODO: Javadoc */
    public Long getReceiverMessageConsumptionTimeout()
    {
        return receiverMessageConsumptionTimeout;
    }

    public void setReceiverMessageConsumptionTimeout(Long receiverMessageConsumptionTimeout)
    {
        this.receiverMessageConsumptionTimeout = receiverMessageConsumptionTimeout;
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
    public EventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(EventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    private String messageTransceiverName;
    private String messageTransceiverLocation;
    private String serviceProviderIdentifier;
    @NotNull(message = "A receiver JMS connection factory must be specified.")
    private ConnectionFactory receiverConnectionFactory;
    @NotNull(message = "The set receiver connection client ID property may not be null")
    private Boolean setReceiverConnectionClientId = true; // Default.
    @NotNull(message = "A receiver JMS destination must be specified.")
    private Destination receiverDestination;
    private PayloadTransformer receiverPayloadTransformer;
    private PayloadDecompressor receiverPayloadDecompressor;
    @NotNull(message = "A transmitter JMS connection factory must be specified.")
    private ConnectionFactory transmitterConnectionFactory;
    @NotNull(message = "The set transmitter connection client ID property may not be null")
    private Boolean setTransmitterConnectionClientId = true; // Default.
    @NotNull(message = "A transmitter JMS destination must be specified.")
    private Destination transmitterDestination;
    private PayloadTransformer transmitterPayloadTransformer;
    private PayloadCompressor transmitterPayloadCompressor;
    @NotNull(message = "An indication of whether the receiver is transacted must be specified.")
    private Boolean receiverTransacted = true; // Default.
    @Min(value = 0, message = "The receiver message consumption timeout may not be less than 0.")
    private Long receiverMessageConsumptionTimeout;
    @NotNull(message = "A message constructor must be specified.")
    private MessageConstructor messageConstructor;
    private EventPublisher eventPublisher;

}