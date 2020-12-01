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

/** The event that is fired when a service result is received. */
@Entity
public class ServiceResultNotReceivedEvent extends ServiceEvent
{

	/** A unique identifier used to identify each service invocation. */
	public String getRequestIdentifier()
	{
		return requestIdentifier;
	}

	public void setRequestIdentifier(String requestIdentifier)
	{
		this.requestIdentifier = requestIdentifier;
	}

	/** TODO: Javadoc */
	public String getElaboration()
	{
		return elaboration;
	}

	public void setElaboration(String elaboration)
	{
		this.elaboration = elaboration;
	}

	@NotEmpty(message="A request identifier must be specified.")
	@Column(nullable = false)
	private String requestIdentifier;
	private String elaboration;

}