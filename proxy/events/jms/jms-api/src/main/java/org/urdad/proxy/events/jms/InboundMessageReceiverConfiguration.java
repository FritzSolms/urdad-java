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

import java.util.concurrent.ExecutorService;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.urdad.events.proxy.EventPublisherProxy;
import org.urdad.proxy.jms.MessageConsumptionGovernor;
import org.urdad.proxy.jms.PayloadDecompressor;
import org.urdad.proxy.jms.PayloadTransformer;

/**
 * TODO: Javadoc
 */
@ValidInboundMessageReceiverConfiguration
public class InboundMessageReceiverConfiguration
{

    /** TODO: Javadoc */
    public String getMessageReceiverName()
    {
        return messageReceiverName;
    }

    public void setMessageReceiverName(String messageReceiverName)
    {
        this.messageReceiverName = messageReceiverName;
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
    public Topic getReceiverTopic()
    {
        return receiverTopic;
    }

    public void setReceiverTopic(Topic receiverTopic)
    {
        this.receiverTopic = receiverTopic;
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
    public Boolean getReceiverDurableConsumer()
    {
        return receiverDurableConsumer;
    }

    public void setReceiverDurableConsumer(Boolean receiverDurableConsumer)
    {
        this.receiverDurableConsumer = receiverDurableConsumer;
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
    public MessageConsumptionGovernor getMessageConsumptionGovernor()
    {
        return messageConsumptionGovernor;
    }

    public void setMessageConsumptionGovernor(MessageConsumptionGovernor messageConsumptionGovernor)
    {
        this.messageConsumptionGovernor = messageConsumptionGovernor;
    }

    /** TODO: Javadoc */
    public String getEventMessageSelector()
    {
        return eventMessageSelector;
    }

    public void setEventMessageSelector(String eventMessageSelector)
    {
        this.eventMessageSelector = eventMessageSelector;
    }

    /** TODO: Javadoc */
    public Long getEventMonitoringRestPeriod()
    {
        return eventMonitoringRestPeriod;
    }

    public void setEventMonitoringRestPeriod(Long eventMonitoringRestPeriod)
    {
        this.eventMonitoringRestPeriod = eventMonitoringRestPeriod;
    }

    /** TODO: Javadoc */
    public EventPublisherProxy getEventPublisherProxy()
    {
        return eventPublisherProxy;
    }

    public void setEventPublisherProxy(EventPublisherProxy eventPublisherProxy)
    {
        this.eventPublisherProxy = eventPublisherProxy;
    }

    /** TODO: Javadoc */
    public ExecutorService getExecutorService()
    {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService)
    {
        this.executorService = executorService;
    }

    private String messageReceiverName;
    @NotNull(message = "A receiver JMS connection factory must be specified.")
    private ConnectionFactory receiverConnectionFactory;
    @NotNull(message = "The set receiver connection client ID property may not be null")
    private Boolean setReceiverConnectionClientId = true; // Default.
    @NotNull(message = "A receiver JMS topic must be specified.")
    private Topic receiverTopic;
    private PayloadTransformer receiverPayloadTransformer;
    private PayloadDecompressor receiverPayloadDecompressor;
    @NotNull(message = "An indication of whether the receiver is transacted must be specified.")
    private Boolean receiverTransacted = true; // Default.
    @NotNull(message = "An indication of whether the receiver is durable consumer must be specified.")
    private Boolean receiverDurableConsumer = true; // Default.
    @Min(value = 0, message = "The receiver message consumption timeout may not be less than 0.")
    private Long receiverMessageConsumptionTimeout;
    private MessageConsumptionGovernor messageConsumptionGovernor;
    private String eventMessageSelector;
    @NotNull(message = "An event monitoring rest period must be specified.")
    @Min(value = 0, message = "An event monitoring not permitted rest period may not be less than 0.")
    private Long eventMonitoringRestPeriod;
    @NotNull(message = "An event publisher proxy must be specified.")
    private EventPublisherProxy eventPublisherProxy;
    @NotNull(message = "An executor service must be specified.")
    private ExecutorService executorService;

}