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
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/events/eventpersistencemanager")
@Stateless(name = "EventPersistenceManagerBeanRpcRestAdaptor")
@Local(EventPersistenceManager.EventPersistenceManagerLocal.class)
public class EventPersistenceManagerImpl implements EventPersistenceManager.EventPersistenceManagerLocal
{

    @Override
    @Path("/persistevent")
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response persistEvent(org.urdad.events.EventPersistenceManager.PersistEventRequest persistEventRequest) throws
            RequestNotValidException,
            org.urdad.events.EventPersistenceManager.EventMustBeRecognisableException,
            org.urdad.events.EventPersistenceManager.EventMustNotBePersistedException
    {
        // Invoke actual service and build HTTP response.
        return buildOkResponse(eventPersistenceManager.persistEvent(persistEventRequest));
    }

    private Response buildOkResponse(org.urdad.services.Response response)
    {
        return Response.ok().header(CustomHttpHeaders.X_RESPONSE_TYPE, response.getClass().getName()).entity(response).build();
    }

    @Inject
    private org.urdad.events.EventPersistenceManager.EventPersistenceManagerLocal eventPersistenceManager;
}