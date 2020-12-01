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

package org.urdad.jpa;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;

/** This class represents the base class for all the 'entity' domain object representations. While there is a general preference to avoid
 * object inheritance, this is one of the few instances where an exception is made.. */
@SuppressWarnings("serial")
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Entity implements Serializable
{

	/** Unique 'object' identifier that is assigned to an entity. */
	public abstract Long getIdentifier();

	public abstract void setIdentifier(Long identifier);

	/** The version property is used to facilitate optimistic locking. */
	public Long getVersion()
	{
		return version;
	}

	public void setVersion(Long version)
	{
		this.version = version;
	}

	/** The timezone-specific moment the entity the entity was . */
	public LocalDateTime getLastModified()
	{
		return lastModified;
	}

	@PrePersist
	@PreUpdate
	protected void setLastModified()
	{
		lastModified = LocalDateTime.now();
	}
	@Version
	protected Long version;
	private LocalDateTime lastModified;

}