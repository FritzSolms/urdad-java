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
import javax.xml.bind.annotation.XmlType;
import javax.validation.constraints.NotEmpty;
import org.urdad.events.Event;

/** The abstract super class for any events that fall within the domain of service provider. */
@SuppressWarnings("serial")
@XmlType
@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class ServiceProviderEvent extends Event
{
	/** Default constructor. */
	public ServiceProviderEvent()
	{
		super();
	}

	/** Convenience constructor. */
	public ServiceProviderEvent(String serviceProviderType)
	{
		this.serviceProviderType = serviceProviderType;
	}

	/** Convenience constructor. */
	public ServiceProviderEvent(String serviceProviderType, String serviceProviderIdentifier)
	{
		this.serviceProviderType = serviceProviderType;
		this.serviceProviderIdentifier = serviceProviderIdentifier;
	}

	/** TODO: Javadoc */
	public String getServiceProviderType()
	{
		return serviceProviderType;
	}

	public void setServiceProviderType(String serviceProviderType)
	{
		this.serviceProviderType = serviceProviderType;
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

	@NotEmpty(message = "A service provider type must be specified.")
	private String serviceProviderType;
	private String serviceProviderIdentifier;

}