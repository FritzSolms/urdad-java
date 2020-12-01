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
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;

/** Provider service contracts that are associated with the event publication domain of responsibility. */
@Service
public class EventPublisherBean implements EventPublisher.EventPublisherLocal, EventPublisher.EventPublisherRemote
{
	@Override
	public PublishEventResponse publishEvent(PublishEventRequest publishEventRequest) throws RequestNotValidException,
		EventMustBeSupportedException
	{
		logger.trace("Publishing Event{}",publishEventRequest.getEvent());
		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(PublishEventRequest.class, publishEventRequest);

		applicationEventPublisher.publishEvent(publishEventRequest.getEvent());

		return new EventPublisher.PublishEventResponse();
	}

	@Async
	@Override
	public Future<PublishEventResponse> publishEventAsync(PublishEventRequest publishEventRequest)
		throws RequestNotValidException, EventMustBeSupportedException
	{
		return new AsyncResult<>(publishEvent(publishEventRequest));
	}

	private static final Logger logger = LoggerFactory.getLogger(EventPublisherBean.class);
	@Inject
	private ServiceValidationUtilities serviceValidationUtilities;
	@Inject
	private ApplicationEventPublisher applicationEventPublisher;

}