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
import org.urdad.services.PreconditionViolation;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;

/** Provider service contracts that are associated with event persistence. */
@SuppressWarnings("serial")
public interface EventPersistenceManager
{
	/** Persists the specified event. */
	PersistEventResponse persistEvent(PersistEventRequest persistEventRequest)
		throws RequestNotValidException, EventMustBeRecognisableException, EventMustNotBePersistedException;

	/** Asynchronous version of the service above. */
	Future<PersistEventResponse> persistEventAsync(PersistEventRequest persistEventRequest)
		throws RequestNotValidException, EventMustBeRecognisableException, EventMustNotBePersistedException;

	@XmlRootElement
	@XmlType
	@XmlAccessorType(XmlAccessType.PROPERTY)
	class PersistEventRequest extends Request
	{
		/** Default constructor. */
		public PersistEventRequest() {}

		/** Convenience constructor. */
		public PersistEventRequest(Event event)
		{
			this.event = event;
		}

		/** The event that must be persisted. */
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
	class PersistEventResponse extends Response
	{}

	/** Thrown to indicate that the 'event must be recognisable' pre-condition has been violated. */
	@XmlRootElement
	@XmlType
	@XmlAccessorType(XmlAccessType.PROPERTY)
	class EventMustBeRecognisableException extends PreconditionViolation
	{

		/** Default constructor. */
		public EventMustBeRecognisableException() {}

		/** Constructs a new <code>EventMustBeRecognisableException</code> exception with the specified detail message.
		 *
		 * @param message the detail message. */
		public EventMustBeRecognisableException(String message)
		{
			super(message);
		}

		/** Constructs a new <code>EventMustBeRecognisableException</code> exception with the specified detail message and cause.
		 *
		 * @param message the detail message.
		 * @param cause the cause. */
		public EventMustBeRecognisableException(String message, Throwable cause)
		{
			super(message, cause);
		}

		/** Constructs a new <code>EventMustBeRecognisableException</code> exception with the specified cause.
		 *
		 * @param cause the cause. */
		public EventMustBeRecognisableException(Throwable cause)
		{
			super(cause);
		}

	}

	/** Thrown to indicate that the 'event must not already be persisted' pre-condition has been violated. */
	@XmlRootElement
	@XmlType
	@XmlAccessorType(XmlAccessType.PROPERTY)
	class EventMustNotBePersistedException extends PreconditionViolation
	{
		/** Default constructor. */
		public EventMustNotBePersistedException() {}

		/** Constructs a new <code>EventMustNotBePersistedException</code> exception with the specified detail message.
		 *
		 * @param message the detail message. */
		public EventMustNotBePersistedException(String message)
		{
			super(message);
		}

		/** Constructs a new <code>EventMustNotBePersistedException</code> exception with the specified detail message and cause.
		 *
		 * @param message the detail message.
		 * @param cause the cause. */
		public EventMustNotBePersistedException(String message, Throwable cause)
		{
			super(message, cause);
		}

		/** Constructs a new <code>EventMustNotBePersistedException</code> exception with the specified cause.
		 *
		 * @param cause the cause. */
		public EventMustNotBePersistedException(Throwable cause)
		{
			super(cause);
		}
	}

	interface EventPersistenceManagerLocal extends EventPersistenceManager {}
	interface EventPersistenceManagerRemote extends EventPersistenceManager {}
}