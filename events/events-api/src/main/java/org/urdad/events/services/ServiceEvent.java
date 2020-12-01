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

/** The abstract super class for any events that fall within the domain of service invocation. */
@SuppressWarnings("serial")
@XmlType
@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class ServiceEvent extends ServiceProviderEvent
{
	/** Default constructor. */
	public ServiceEvent()
	{
		super();
	}

	/** Convenience constructor. */
	public ServiceEvent(String serviceProviderType, String service)
	{
		super(serviceProviderType);
		this.service = service;
	}

	/** Convenience constructor. */
	public ServiceEvent(String serviceProviderType, String serviceProviderIdentifier, String service)
	{
		super(serviceProviderType, serviceProviderIdentifier);
		this.service = service;
	}

	/** TODO: Javadoc */
	public String getService()
	{
		return service;
	}

	public void setService(String service)
	{
		this.service = service;
	}

	@NotEmpty(message = "A service name must be specified.")
	private String service;

}