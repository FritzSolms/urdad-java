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

public class EventPublisherInboundProxy
		implements EventPublisherProxy.EventPublisherProxyLocal, EventPublisherProxy.EventPublisherProxyRemote
{
	public EventPublisherInboundProxy(EventPublisher eventPublisher)
	{
		super();
		this.eventPublisher = eventPublisher;
	}

	@Override
	public PublishEventResponse publishEvent(PublishEventRequest publishEventRequest) throws Throwable
	{
		eventPublisher.publishEvent(new EventPublisher.PublishEventRequest(publishEventRequest.getEvent()));

		return new PublishEventResponse();
	}

	private EventPublisher eventPublisher;
}
