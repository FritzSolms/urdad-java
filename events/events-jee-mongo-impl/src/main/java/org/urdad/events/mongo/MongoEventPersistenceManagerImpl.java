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

package org.urdad.events.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.util.concurrent.Future;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.events.EventPersistenceManager;
import org.urdad.json.binding.Marshaller;
import org.urdad.mongo.MongoUtilities;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;

//@Stateless
//@Local(EventPersistenceManager.EventPersistenceManagerLocal.class)
//@Remote(EventPersistenceManager.EventPersistenceManagerRemote.class)
public class MongoEventPersistenceManagerImpl implements EventPersistenceManager.EventPersistenceManagerLocal, EventPersistenceManager
   .EventPersistenceManagerRemote
{

    @Override
    public PersistEventResponse persistEvent(PersistEventRequest persistEventRequest) throws RequestNotValidException,
        EventMustBeRecognisableException, EventMustNotBePersistedException
    {
        // Check pre-condition: Request must be valid.
        serviceValidationUtilities.validateRequest(PersistEventRequest.class, persistEventRequest);

        String collectionName = persistEventRequest.getEvent().getClass().getName();

        if (!mongoUtilities.collectionExists(mongoDatabase, collectionName))
        {
            mongoDatabase.createCollection(collectionName);
        }

        Document eventDocument;
        try
        {
            eventDocument = Document.parse(marshaller.marshallJavaObjectToJson(new Marshaller.MarshallJavaObjectToJsonRequest
                (persistEventRequest.getEvent())).getJson());
        }
        catch (Marshaller.JavaObjectNotValidException e)
        {
            // Pre-condition: Java object must be valid.
            throw new EventMustBeRecognisableException(e);
        }

System.out.println("eventDocument.getObjectId() = " + eventDocument.getObjectId("_id"));

        mongoDatabase.getCollection(collectionName).insertOne(eventDocument);

System.out.println("eventDocument.getObjectId() = " + eventDocument.getObjectId("_id"));

        // Create service response.
        return new PersistEventResponse();
    }

    @Override
    @Asynchronous
    public Future<PersistEventResponse> persistEventAsync(PersistEventRequest persistEventRequest)
        throws RequestNotValidException, EventMustBeRecognisableException, EventMustNotBePersistedException
    {
        return new AsyncResult<>(eventPersistenceManager.persistEvent(persistEventRequest));
    }

    @PostConstruct
    private void postConstruct()
    {
        mongoUtilities = new MongoUtilities();
        mongoClient = new MongoClient();
        mongoDatabase = mongoClient.getDatabase(mongoDatabaseName);
    }

    @PreDestroy
    private void preDestroy()
    {
        mongoClient.close();
    }

    private static final Logger logger = LoggerFactory.getLogger(MongoEventPersistenceManagerImpl.class);
    // Fallback on @EJB. @Inject does not deal with circular dependency issues associated with 'self' injection.
//    @EJB
    private EventPersistenceManagerLocal eventPersistenceManager;
//    @Inject
    private ServiceValidationUtilities serviceValidationUtilities;
//    @Inject
    private Marshaller.MarshallerLocal marshaller;
//    @Resource(name="java:app/env/mongoDatabaseName")
    private String mongoDatabaseName;
    private MongoUtilities mongoUtilities;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

}