package org.urdad.proxy.websocket;

import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;

import javax.websocket.Session;
import java.util.concurrent.Future;

public interface WebsocketSessionProvider {
    /**
     * TODO Javadoc
     */
    public ProvideSessionResponse provideSession(ProvideSessionRequest provideSessionRequest) throws RequestNotValidException;

    public Future<ProvideSessionResponse> provideSessionAsync(ProvideSessionRequest provideSessionRequest) throws RequestNotValidException;

    class ProvideSessionRequest extends Request {

    }

    class ProvideSessionResponse extends Response {
        private Session session;

        public Session getSession() {
            return session;
        }

        public void setSession(Session session) {
            this.session = session;
        }

        public ProvideSessionResponse() {
        }

        public ProvideSessionResponse(Session session) {

            this.session = session;
        }
    }

    /** TODO Javadoc */
    public SetSessionResponse setSession(SetSessionRequest setSessionRequest) throws RequestNotValidException;
    public Future<SetSessionResponse> setSessionAsync(SetSessionRequest setSessionRequest) throws RequestNotValidException;

    class SetSessionRequest extends Request{
        private Session session;

        public Session getSession() {
            return session;
        }

        public void setSession(Session session) {
            this.session = session;
        }

        public SetSessionRequest() {
        }

        public SetSessionRequest(Session session) {

            this.session = session;
        }
    }
    class SetSessionResponse extends Response{

    }

    interface WebsocketSessionProviderLocal extends WebsocketSessionProvider{}
    interface WebsocketSessionProviderRemote extends WebsocketSessionProvider{}
}
