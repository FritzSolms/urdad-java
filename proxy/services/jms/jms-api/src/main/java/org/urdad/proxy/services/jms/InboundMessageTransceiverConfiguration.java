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

import java.util.concurrent.ExecutorService;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.urdad.proxy.jms.MessageConsumptionGovernor;
import org.urdad.services.mask.ServiceMask;

/**
 * TODO: Javadoc
 */
@ValidInboundMessageTransceiverConfiguration
public class InboundMessageTransceiverConfiguration extends MessageTransceiverConfiguration
{

    /** TODO: Javadoc */
    public Boolean getSelectByServiceProviderTypeProperty()
    {
        return selectByServiceProviderTypeProperty;
    }

    public void setSelectByServiceProviderTypeProperty(Boolean selectByServiceProviderTypeProperty)
    {
        this.selectByServiceProviderTypeProperty = selectByServiceProviderTypeProperty;
    }

    /** TODO: Javadoc */
    public Boolean getOnlySelectByServiceProviderIdentifierProperty()
    {
        return onlySelectByServiceProviderIdentifierProperty;
    }

    public void setOnlySelectByServiceProviderIdentifierProperty(Boolean onlySelectByServiceProviderIdentifierProperty)
    {
        this.onlySelectByServiceProviderIdentifierProperty = onlySelectByServiceProviderIdentifierProperty;
    }

    /** TODO: Javadoc */
    public Boolean getSelectByServiceProviderIdentifierPropertyAndNoServiceProviderIdentifierProperty()
    {
        return selectByServiceProviderIdentifierPropertyAndNoServiceProviderIdentifierProperty;
    }

    public void setSelectByServiceProviderIdentifierPropertyAndNoServiceProviderIdentifierProperty(Boolean
                                                                                                       selectByServiceProviderIdentifierPropertyAndNoServiceProviderIdentifierProperty)
    {
        this.selectByServiceProviderIdentifierPropertyAndNoServiceProviderIdentifierProperty =
            selectByServiceProviderIdentifierPropertyAndNoServiceProviderIdentifierProperty;
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
    public String getRequestMessageSelector()
    {
        return requestMessageSelector;
    }

    public void setRequestMessageSelector(String requestMessageSelector)
    {
        this.requestMessageSelector = requestMessageSelector;
    }

    /** TODO: Javadoc */
    public Boolean getApplyResultIndicatorProperty()
    {
        return applyResultIndicatorProperty;
    }

    public void setApplyResultIndicatorProperty(Boolean applyResultIndicatorProperty)
    {
        this.applyResultIndicatorProperty = applyResultIndicatorProperty;
    }

    /** TODO: Javadoc */
    public Long getRequestMonitoringRestPeriod()
    {
        return requestMonitoringRestPeriod;
    }

    public void setRequestMonitoringRestPeriod(Long requestMonitoringRestPeriod)
    {
        this.requestMonitoringRestPeriod = requestMonitoringRestPeriod;
    }

    /** TODO: Javadoc */
    public ServiceMask getServiceMask()
    {
        return serviceMask;
    }

    public void setServiceMask(ServiceMask serviceMask)
    {
        this.serviceMask = serviceMask;
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

    private MessageConsumptionGovernor messageConsumptionGovernor;
    @NotNull(message = "The select by service provider type property may not be null")
    private Boolean selectByServiceProviderTypeProperty = true; // Default.
    @NotNull(message = "The only select by service provider identifier property may not be null")
    private Boolean onlySelectByServiceProviderIdentifierProperty = false; // Default.
    @NotNull(message = "The select by service provider identifier property and no service property identifier property may not be null")
    private Boolean selectByServiceProviderIdentifierPropertyAndNoServiceProviderIdentifierProperty = false; // Default.
    private String requestMessageSelector;
    @NotNull(message = "The apply result indicator property may not be null")
    private Boolean applyResultIndicatorProperty = true; // Default;
    @NotNull(message = "A request monitoring rest period must be specified.")
    @Min(value = 0, message = "A request monitoring not permitted rest period may not be less than 0.")
    private Long requestMonitoringRestPeriod;
    @NotNull(message = "A service provider proxy must be specified.")
    private ServiceMask serviceMask;
    @NotNull(message = "An executor service must be specified.")
    private ExecutorService executorService;

}