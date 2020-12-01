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

import java.util.concurrent.Future;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.urdad.services.Response;
import org.urdad.services.PreconditionViolation;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;

/** Provider service contracts that are associated with the event publication domain of responsibility. */
@SuppressWarnings("serial")
public interface EventPublisher
{
	/** Publishes the specified event. */
	PublishEventResponse publishEvent(PublishEventRequest publishEventRequest) throws RequestNotValidException,
		EventMustBeSupportedException;

	/** Asynchronous version of the service above. */
	Future<PublishEventResponse> publishEventAsync(PublishEventRequest publishEventRequest) throws
		RequestNotValidException, EventMustBeSupportedException;

	@XmlRootElement
	@XmlType
	@XmlAccessorType(XmlAccessType.PROPERTY)
	class PublishEventRequest extends Request
	{
		/** Default constructor. */
		public PublishEventRequest(){}

		/** Convenience constructor. */
		public PublishEventRequest(Event event)
		{
			this.event = event;
		}

		/** The event that must be published. */
		public Event getEvent()
		{
			return event;
		}

		public void setEvent(Event event)
		{
			this.event = event;
		}

		@Valid
		@NotNull(message = "An event must be specified.")
		private Event event;
	}

	@XmlRootElement
	@XmlType
	@XmlAccessorType(XmlAccessType.PROPERTY)
	class PublishEventResponse extends Response{}

	/** Thrown to indicate that the event must be supported pre-condition has been violated. */
	@XmlRootElement
	@XmlType
	@XmlAccessorType(XmlAccessType.PROPERTY)
	class EventMustBeSupportedException extends PreconditionViolation
	{
		/** Default constructor. */
		public EventMustBeSupportedException() {}

		/** Constructs a new <code>EventMustBeSupportedException</code> exception with the specified detail message and cause. */
		public EventMustBeSupportedException(String message, Throwable cause)
		{
			super(message, cause);
		}

		/** Constructs a new <code>EventMustBeSupportedException</code> exception with the specified detail message. */
		public EventMustBeSupportedException(String message)
		{
			super(message);
		}

		/** Constructs a new <code>EventMustBeSupportedException</code> exception with the specified cause. */
		public EventMustBeSupportedException(Throwable cause)
		{
			super(cause);
		}
	}

	interface EventPublisherLocal extends EventPublisher{}
	interface EventPublisherRemote extends EventPublisher{}

}