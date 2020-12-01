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
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;
import org.urdad.services.mask.ServiceMask;

import java.util.concurrent.Future;

public class EventPublisherFromMaskBinding
		implements ServiceMask.ServiceMaskLocal, ServiceMask.ServiceMaskRemote
{
	public EventPublisherFromMaskBinding(EventPublisher eventPublisher)
	{
		super();
		this.eventPublisher = eventPublisher;
	}

	@Override
	public Response invokeService(Request request) throws Throwable
	{

		if (request instanceof EventPublisher.PublishEventRequest)
		{
			return eventPublisher.publishEvent((EventPublisher.PublishEventRequest) request);
		}
		else
		{
			throw new RequestNotValidException("Service request '" + request.getClass().getName() + "' is not " + "supported.");
		}
	}

    @Override
    public Future<Response> invokeServiceAsync(Request request) throws Throwable {
        throw new UnsupportedOperationException("EventPublisherFromMaskBinding does not support async invocation");
    }

    @Override
	public RetrieveServiceProviderTypeResponse retrieveServiceProviderType(
			RetrieveServiceProviderTypeRequest retrieveServiceProviderTypeRequest)
	{
		return new RetrieveServiceProviderTypeResponse(eventPublisher.getClass());
	}

	private EventPublisher eventPublisher;
}
