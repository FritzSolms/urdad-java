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

package org.urdad.events.jpa;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import org.urdad.jpa.converters.LocalDateTimeConverter;

/** Introduces the abstract concept of an event. An event is used to notify stake holders of something happening. */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "EVENT_SEQUENCE", sequenceName = "EVENT_SEQ")
public abstract class Event extends org.urdad.jpa.Entity
{

	/** Unique 'object' identifier that is assigned to an entity. */
	public Long getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(Long identifier)
	{
		this.identifier = identifier;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	@Convert(converter = LocalDateTimeConverter.class)
	public LocalDateTime getDate()
	{
		return date;
	}

	public void setDate(LocalDateTime date)
	{
		this.date = date;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EVENT_SEQUENCE")
	private Long identifier;
	private String location;
	@NotNull(message = "A date must be specified.")
	@Column(nullable = false)
	private LocalDateTime date;

}