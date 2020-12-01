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

package org.urdad.events.adapter.rpcrest;

import org.urdad.jaxrs.adapter.rpcrest.CustomHttpHeaders;
import org.urdad.services.RequestNotValidException;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/events/eventpublisher")
@Stateless(name = "EventPublisherBeanRpcRestAdaptor")
@Local(EventPublisher.EventPublisherLocal.class)
public class EventPublisherImpl implements EventPublisher.EventPublisherLocal
{
	/** Determine default options. */
	@Override
	@OPTIONS
	@Path("{path:.*}")
	public Response determineDefaultOptions()
	{
		// Construct HTTP response.
		return Response.ok().build();
	}

	@Override
	@Path("/publishevent")
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response publishEvent(org.urdad.events.EventPublisher.PublishEventRequest publishEventRequest)
			throws RequestNotValidException, org.urdad.events.EventPublisher.EventMustBeSupportedException
	{
		// Invoke actual service and build HTTP response.
		return buildOkResponse(eventPublisher.publishEvent(publishEventRequest));
	}

	private Response buildOkResponse(org.urdad.services.Response response)
	{
		return Response.ok().header(CustomHttpHeaders.X_RESPONSE_TYPE, response.getClass().getName()).entity(response).build();
	}

	@Inject
	private org.urdad.events.EventPublisher.EventPublisherLocal eventPublisher;

}