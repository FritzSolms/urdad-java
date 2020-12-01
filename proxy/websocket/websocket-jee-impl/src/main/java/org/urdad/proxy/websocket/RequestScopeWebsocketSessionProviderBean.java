package org.urdad.proxy.websocket;

import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.websocket.Session;
import java.util.concurrent.Future;

@RequestScoped
public class RequestScopeWebsocketSessionProviderBean implements WebsocketSessionProvider.WebsocketSessionProviderLocal {
    @Override
    public ProvideSessionResponse provideSession(ProvideSessionRequest provideSessionRequest) throws RequestNotValidException {
        serviceValidationUtilities.validateRequest(ProvideSessionRequest.class, provideSessionRequest);
        return new ProvideSessionResponse(session);
    }

    @Override
    public Future<ProvideSessionResponse> provideSessionAsync(ProvideSessionRequest provideSessionRequest) throws RequestNotValidException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SetSessionResponse setSession(SetSessionRequest setSessionRequest) throws RequestNotValidException {
        serviceValidationUtilities.validateRequest(SetSessionRequest.class, setSessionRequest);
        session = setSessionRequest.getSession();
        return new SetSessionResponse();
    }

    @Override
    public Future<SetSessionResponse> setSessionAsync(SetSessionRequest setSessionRequest) throws RequestNotValidException {
        throw new UnsupportedOperationException();
    }

    private Session session;
    @Inject
    private ServiceValidationUtilities serviceValidationUtilities;
}
