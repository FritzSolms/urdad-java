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

package org.urdad.events.services.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;

/** FIXME: Javadoc */
@Entity
public abstract class ServiceProviderEvent extends org.urdad.events.jpa.Event
{

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
	@Column(nullable = false)
	private String serviceProviderType;
	private String serviceProviderIdentifier;

}