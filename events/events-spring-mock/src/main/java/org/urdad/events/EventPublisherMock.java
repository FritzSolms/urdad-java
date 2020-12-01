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

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.mocking.Mock;
import org.urdad.services.mocking.resultsbystateorrequest.JaxbXmlRepresentationRequestResultKey;
import org.urdad.services.mocking.resultsbystateorrequest.ResultsByStateOrRequestMock;
import org.urdad.validation.services.ServiceValidationUtilities;
import org.urdad.xml.binding.ClassesToBeBound;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.concurrent.Future;

/**
 * Provider service contracts that are associated with the event publication domain of responsibility.
 */
@Service
public class EventPublisherMock extends ResultsByStateOrRequestMock
		implements EventPublisher.EventPublisherLocal, EventPublisher.EventPublisherRemote
{

	@Override
	public PublishEventResponse publishEvent(PublishEventRequest publishEventRequest)
			throws RequestNotValidException, EventMustBeSupportedException
	{
		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(PublishEventRequest.class, publishEventRequest);
		try
		{
			return triggerResult(PublishEventResponse.class,
					new JaxbXmlRepresentationRequestResultKey(publishEventRequest, classesToBeBound.getClassesToBeBound()));
		} catch (Throwable throwable)
		{
			if (throwable instanceof RequestNotValidException)
			{
				throw (RequestNotValidException) throwable;
			}
			if (throwable instanceof EventMustBeSupportedException)
			{
				throw (EventMustBeSupportedException) throwable;
			}
			else
			{
				// We want a class cast exception if throwable is not a system error at this point.
				throw (RuntimeException) throwable;
			}
		}
	}

	@Async
	@Override
	public Future<PublishEventResponse> publishEventAsync(PublishEventRequest publishEventRequest)
			throws RequestNotValidException, EventMustBeSupportedException
	{
		return new AsyncResult<>(this.publishEvent(publishEventRequest));
	}

	@PostConstruct
	public void postConstruct()
	{
		setState(State.externalRequirementsMet);
	}

	public enum State implements Mock.State
	{
		externalRequirementsMet,
		unexpectedServiceError
	}

	@Inject
	private ServiceValidationUtilities serviceValidationUtilities;

	@Inject
	@org.urdad.events.binding.ClassesToBeBound
	private ClassesToBeBound classesToBeBound;
}