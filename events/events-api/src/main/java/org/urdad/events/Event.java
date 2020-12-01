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

package org.urdad.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.urdad.xml.binding.adapter.LocalDateTimeAdapter;

/** Introduces the abstract concept of an event. Provides stakeholders with a notification of an event having occurred. */
@SuppressWarnings("serial")
@XmlTransient
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class Event implements Serializable
{
	/** The time when the event was originally produced.  */
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime getDate()
	{
		return date;
	}

	public void setDate(LocalDateTime date)
	{
		this.date = date;
	}

	public Event date(LocalDateTime date)
	{
		this.date = date;
		return this;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public Event location(String location)
	{
		this.location = location;
		return this;
	}

	@NotNull(message = "A indication of when the event was produced must be specified.")
	private LocalDateTime date = LocalDateTime.now(); // Default value.
	private String location;

}