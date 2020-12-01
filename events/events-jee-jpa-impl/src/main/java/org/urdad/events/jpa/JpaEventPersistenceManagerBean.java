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

package org.urdad.events.jpa;

import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.urdad.events.Event;
import org.urdad.events.EventPersistenceManager;
import org.urdad.jpa.factory.EntityFactory;
import org.urdad.jpa.factory.EntityFactoryContainer;
import org.urdad.jpa.factory.SystemSpecificEntityFactoryContainer;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Stateless
@Local(EventPersistenceManager.EventPersistenceManagerLocal.class)
@Remote(EventPersistenceManager.EventPersistenceManagerRemote.class)
public class JpaEventPersistenceManagerBean implements EventPersistenceManager.EventPersistenceManagerLocal,
	EventPersistenceManager.EventPersistenceManagerRemote
{

	@Override
	public PersistEventResponse persistEvent(PersistEventRequest persistEventRequest)
		throws RequestNotValidException, EventMustBeRecognisableException, EventMustNotBePersistedException
	{
		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(PersistEventRequest.class, persistEventRequest);

		Event event = persistEventRequest.getEvent();

		// Persist event to database.
		try
		{
			EntityFactory entityFactory = entityFactoryContainer.retrieveEntityFactory(event.getClass());
			if (entityFactory == null)
			{
				// Pre-condition: Event must be recognisable.
				throw new EventMustBeRecognisableException(
					"Events of type '" + event.getClass().getName() + "' are currently not " + "supported.");
			}

			entityManager.persist(entityFactory.produceEntity(event));

			// Create service response.
			return new PersistEventResponse();
		}
		catch (EntityExistsException e)
		{
			throw new EventMustNotBePersistedException("The specified event has already been persisted.");
		}
	}

	@Override
	@Asynchronous
	public Future<PersistEventResponse> persistEventAsync(PersistEventRequest persistEventRequest)
		throws RequestNotValidException, EventMustBeRecognisableException, EventMustNotBePersistedException
	{
		return new AsyncResult<>(eventPersistenceManager.persistEvent(persistEventRequest));
	}

	/** This method is for unit testing purposes only, to allow setting the entity manager. */
	public void setEntityManager(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	// Fall back on @EJB. @Inject does not deal with circular dependency issues associated with 'self' injection.
	@EJB
	private EventPersistenceManagerLocal eventPersistenceManager;
	@Inject
	private ServiceValidationUtilities serviceValidationUtilities;
	@PersistenceContext
	private EntityManager entityManager;
	@Inject
	@SystemSpecificEntityFactoryContainer
	private EntityFactoryContainer entityFactoryContainer;

}