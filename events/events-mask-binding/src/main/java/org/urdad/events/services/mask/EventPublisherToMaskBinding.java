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

package org.urdad.events.services.mask;

import org.urdad.events.EventPublisher;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.mask.ServiceMask;

import java.util.concurrent.Future;

public class EventPublisherToMaskBinding implements EventPublisher.EventPublisherLocal, EventPublisher.EventPublisherRemote
{

	public EventPublisherToMaskBinding(ServiceMask serviceMask)
	{
		super();
		this.serviceMask = serviceMask;
	}

	@Override
	public PublishEventResponse publishEvent(PublishEventRequest publishEventRequest)
			throws RequestNotValidException, EventMustBeSupportedException
	{
		try
		{
			return (PublishEventResponse) serviceMask
					.invokeService((publishEventRequest));
		} catch (Throwable t)
		{
			if (t instanceof RequestNotValidException)
			{
				throw (RequestNotValidException) t;
			}
			else if (t instanceof EventMustBeSupportedException)
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
		try {
			return (Future<PublishEventResponse>)(serviceMask.invokeServiceAsync(publishEventRequest));
		} catch (Throwable t) {
			if (t instanceof RequestNotValidException)
			{
				throw (RequestNotValidException) t;
			}
			else if (t instanceof EventMustBeSupportedException)
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

	public ServiceMask getServiceMask()
	{
		return serviceMask;
	}

	private ServiceMask serviceMask;

}
