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

import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.urdad.proxy.jms.PropertySetter;

/**
 * TODO: Javadoc
 */
@ValidOutboundMessageTransceiverConfiguration
public class OutboundMessageTransceiverConfiguration extends MessageTransceiverConfiguration
{

    /** TODO: Javadoc */
    public Boolean getApplyServiceProviderTypeProperty()
    {
        return applyServiceProviderTypeProperty;
    }

    public void setApplyServiceProviderTypeProperty(Boolean applyServiceProviderTypeProperty)
    {
        this.applyServiceProviderTypeProperty = applyServiceProviderTypeProperty;
    }

    /** TODO: Javadoc */
    public Boolean getApplyServiceProviderIdentifierProperty()
    {
        return applyServiceProviderIdentifierProperty;
    }

    public void setApplyServiceProviderIdentifierProperty(Boolean applyServiceProviderIdentifierProperty)
    {
        this.applyServiceProviderIdentifierProperty = applyServiceProviderIdentifierProperty;
    }

    /** TODO: Javadoc */
    public Boolean getSelectByCorrelationIdAndResultIndicator()
    {
        return selectByCorrelationIdAndResultIndicator;
    }

    public void setSelectByCorrelationIdAndResultIndicator(Boolean selectByCorrelationIdAndResultIndicator)
    {
        this.selectByCorrelationIdAndResultIndicator = selectByCorrelationIdAndResultIndicator;
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

    /** TODO: Javadoc */
    public RequestIdentifierExposer getRequestIdentifierExposer()
    {
        return requestIdentifierExposer;
    }

    public void setRequestIdentifierExposer(RequestIdentifierExposer requestIdentifierExposer)
    {
        this.requestIdentifierExposer = requestIdentifierExposer;
    }

    @NotNull(message = "The apply service provider type property may not be null")
    private Boolean applyServiceProviderTypeProperty = true; // Default. (Value derived from request during invocation)
    @NotNull(message = "The apply service provider identifier property may not be null")
    private Boolean applyServiceProviderIdentifierProperty = false; // Default.
    // Useful when using same JMS destination for both requests or results (responses, exceptions or errors).
    @NotNull()
    private Boolean selectByCorrelationIdAndResultIndicator = true; // Default;
    @NotNull(message = "The set used for property setters may not be null.")
    private Set<PropertySetter> propertySetters = new HashSet<>();
    private RequestIdentifierExposer requestIdentifierExposer;

}