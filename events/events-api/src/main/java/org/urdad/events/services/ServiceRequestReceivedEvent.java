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

package org.urdad.events.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.NotEmpty;

/** The event that is fired when a service request is received. */
@SuppressWarnings("serial")
@XmlRootElement
@XmlType
@XmlAccessorType(XmlAccessType.PROPERTY)
@JsonTypeName("org.urdad.events.services.ServiceRequestReceivedEvent")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceRequestReceivedEvent extends ServiceEvent
{

	/** Default constructor. */
	public ServiceRequestReceivedEvent()
	{
		super();
	}

	/** Convenience constructor. */
	public ServiceRequestReceivedEvent(String serviceProviderType, String serviceProviderIdentifier, String service,
		String requestIdentifier)
	{
		super(serviceProviderType, serviceProviderIdentifier, service);
		this.requestIdentifier = requestIdentifier;
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

	@NotEmpty(message="A request identifier must be specified.")
	private String requestIdentifier;

}