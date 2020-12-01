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

package org.urdad.events.proxy.events;

import org.urdad.events.EventPublisher;
import org.urdad.events.proxy.EventPublisherProxy;
import org.urdad.services.RequestNotValidException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class EventPublisherOutboundProxy implements EventPublisher.EventPublisherLocal, EventPublisher.EventPublisherRemote
{
	public EventPublisherOutboundProxy(EventPublisherProxy eventPublisherProxy, ExecutorService executorService)
	{
		super();
		this.eventPublisherProxy = eventPublisherProxy;
		this.executorService = executorService;
	}

	@Override
	public PublishEventResponse publishEvent(PublishEventRequest publishEventRequest)
			throws RequestNotValidException, EventMustBeSupportedException
	{
		try
		{
			eventPublisherProxy.publishEvent(new EventPublisherProxy.PublishEventRequest(publishEventRequest.getEvent()));

			return new PublishEventResponse();

		} catch (Throwable t)
		{
			if (t instanceof RequestNotValidException)
			{
				throw (RequestNotValidException) t;
			}
			if (t instanceof EventMustBeSupportedException)
			{
				throw (EventMustBeSupportedException) t;
			}
			else if (t instanceof RuntimeException)
			{
				throw (RuntimeException) t;
			}
			else
			{
				throw new RuntimeException(t);
			}
		}
	}

	@Override
	public Future<PublishEventResponse> publishEventAsync(PublishEventRequest publishEventRequest)
			throws RequestNotValidException, EventMustBeSupportedException
	{
		return executorService.submit(() -> publishEvent(publishEventRequest));
	}

	public EventPublisherProxy getEventPublisherProxy()
	{
		return eventPublisherProxy;
	}

	private EventPublisherProxy eventPublisherProxy;
	private ExecutorService executorService;
}
