package org.urdad.proxy.websocket;

import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Singleton
@Local(WebsocketSessionPersistenceManager.WebsocketSessionPersistenceManagerLocal.class)
@Remote(WebsocketSessionPersistenceManager.WebsocketSessionPersistenceManagerRemote.class)
public class InMemoryWebsocketSessionPersistenceManagerBean implements WebsocketSessionPersistenceManager.WebsocketSessionPersistenceManagerLocal, WebsocketSessionPersistenceManager.WebsocketSessionPersistenceManagerRemote {
    @Override
    public FindWebsocketSessionResponse findWebsocketSession(FindWebsocketSessionRequest findWebsocketSessionRequest) throws RequestNotValidException {
        serviceValidationUtilities.validateRequest(FindWebsocketSessionRequest.class,findWebsocketSessionRequest);
        return new FindWebsocketSessionResponse(sessionMap.get(findWebsocketSessionRequest.getSessionId()));
    }

    @Override
    public Future<FindWebsocketSessionResponse> findWebsocketSessionAsync(FindWebsocketSessionRequest findWebsocketSessionRequest) throws RequestNotValidException {
        throw new UnsupportedOperationException();
    }

    @Override
    public RemoveWebsocketSessionResponse removeWebsocketSession(RemoveWebsocketSessionRequest removeWebsocketSessionRequest) throws RequestNotValidException, WebsocketSessionDoesNotExistException {
        serviceValidationUtilities.validateRequest(RemoveWebsocketSessionRequest.class,removeWebsocketSessionRequest);
        sessionMap.remove(removeWebsocketSessionRequest.getSession().getId());

        return new RemoveWebsocketSessionResponse();
    }

    @Override
    public Future<RemoveWebsocketSessionResponse> removeWebsocketSessionAsync(RemoveWebsocketSessionRequest removeWebsocketSessionRequest) throws RequestNotValidException, WebsocketSessionDoesNotExistException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InsertWebsocketSessionResponse insertWebsocketSession(InsertWebsocketSessionRequest insertWebsocketSessionRequest) throws RequestNotValidException, WebsocketSessionAlreadytExistsException {
        serviceValidationUtilities.validateRequest(InsertWebsocketSessionRequest.class,insertWebsocketSessionRequest);
        sessionMap.put(insertWebsocketSessionRequest.getSession().getId(),insertWebsocketSessionRequest.getSession());

        return new InsertWebsocketSessionResponse();
    }

    @Override
    public Future<InsertWebsocketSessionResponse> insertWebsocketSessionAsync(InsertWebsocketSessionRequest insertWebsocketSessionRequest) throws RequestNotValidException, WebsocketSessionAlreadytExistsException {
        throw new UnsupportedOperationException();
    }

    private Map<String,Session> sessionMap = new HashMap<>();

    @Inject
    private ServiceValidationUtilities serviceValidationUtilities;
}
