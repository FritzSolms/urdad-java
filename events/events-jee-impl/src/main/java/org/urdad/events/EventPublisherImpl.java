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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;

import javax.ejb.*;
import javax.inject.Inject;
import java.util.concurrent.Future;

@Stateless
@Local(EventPublisher.EventPublisherLocal.class)
@Remote(EventPublisher.EventPublisherRemote.class)
public class EventPublisherImpl implements EventPublisher.EventPublisherLocal, EventPublisher.EventPublisherRemote
{
	@Override
	public PublishEventResponse publishEvent(PublishEventRequest publishEventRequest)
		throws RequestNotValidException, EventMustBeSupportedException
	{
		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(EventPublisher.PublishEventRequest.class, publishEventRequest);

		event.fire(publishEventRequest.getEvent());

		// Create service response.
		return new PublishEventResponse();
	}

	@Override
	public Future<PublishEventResponse> publishEventAsync(PublishEventRequest publishEventRequest)
		throws RequestNotValidException, EventMustBeSupportedException
	{
		return new AsyncResult<>(eventPublisher.publishEvent(publishEventRequest));
	}

	private static final Logger logger = LoggerFactory.getLogger(EventPublisherImpl.class);
	// Fallback on @EJB. @Inject does not deal with circular dependency issues associated with 'self' injection.
	@EJB
	private EventPublisher.EventPublisherLocal eventPublisher;
	@Inject
	private ServiceValidationUtilities serviceValidationUtilities;
	@Inject
	private javax.enterprise.event.Event<Event> event;

}