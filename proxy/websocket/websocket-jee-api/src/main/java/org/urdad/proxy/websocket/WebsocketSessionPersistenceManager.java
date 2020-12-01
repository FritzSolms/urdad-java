package org.urdad.proxy.websocket;

import org.urdad.services.PreconditionViolation;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.websocket.Session;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.concurrent.Future;

public interface WebsocketSessionPersistenceManager {

    /** TODO Javadoc */
    public FindWebsocketSessionResponse findWebsocketSession(FindWebsocketSessionRequest findWebsocketSessionRequest) throws RequestNotValidException;
    public Future<FindWebsocketSessionResponse> findWebsocketSessionAsync(FindWebsocketSessionRequest findWebsocketSessionRequest) throws RequestNotValidException;

    class FindWebsocketSessionRequest extends Request {
        @NotNull
        @Size(min=1)
        private String sessionId;

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public FindWebsocketSessionRequest(String sessionId) {
            this.sessionId = sessionId;
        }

        public FindWebsocketSessionRequest() {
        }
    }
    class FindWebsocketSessionResponse extends Response {
        private Session session;

        public Session getSession() {
            return session;
        }

        public void setSession(Session session) {
            this.session = session;
        }

        public FindWebsocketSessionResponse() {
        }

        public FindWebsocketSessionResponse(Session session) {

            this.session = session;
        }
    }

    /** TODO Javadoc */
    public RemoveWebsocketSessionResponse removeWebsocketSession(RemoveWebsocketSessionRequest removeWebsocketSessionRequest) throws RequestNotValidException,WebsocketSessionDoesNotExistException;
    public Future<RemoveWebsocketSessionResponse> removeWebsocketSessionAsync(RemoveWebsocketSessionRequest removeWebsocketSessionRequest) throws RequestNotValidException,WebsocketSessionDoesNotExistException;

    class RemoveWebsocketSessionRequest extends Request{
        private Session session;

        public Session getSession() {
            return session;
        }

        public void setSession(Session session) {
            this.session = session;
        }

        public RemoveWebsocketSessionRequest() {
        }

        public RemoveWebsocketSessionRequest(Session session) {

            this.session = session;
        }
    }
    class RemoveWebsocketSessionResponse extends Response{

    }

    /** TODO Javadoc */
    public InsertWebsocketSessionResponse insertWebsocketSession(InsertWebsocketSessionRequest insertWebsocketSessionRequest) throws RequestNotValidException,WebsocketSessionAlreadytExistsException;
    public Future<InsertWebsocketSessionResponse> insertWebsocketSessionAsync(InsertWebsocketSessionRequest insertWebsocketSessionRequest) throws RequestNotValidException,WebsocketSessionAlreadytExistsException;

    class InsertWebsocketSessionRequest extends Request{
        private Session session;

        public Session getSession() {
            return session;
        }

        public void setSession(Session session) {
            this.session = session;
        }

        public InsertWebsocketSessionRequest() {
        }

        public InsertWebsocketSessionRequest(Session session) {

            this.session = session;
        }
    }
    class InsertWebsocketSessionResponse extends Response{

    }

    @XmlRootElement
    @XmlType
    @XmlAccessorType(XmlAccessType.PROPERTY)
    class WebsocketSessionAlreadytExistsException extends PreconditionViolation
    {
        public WebsocketSessionAlreadytExistsException() {}

        public WebsocketSessionAlreadytExistsException(String message)
        {
            super(message);
        }

        public WebsocketSessionAlreadytExistsException(String message, Throwable cause)
        {
            super(message, cause);
        }

        public WebsocketSessionAlreadytExistsException(Throwable cause)
        {
            super(cause);
        }
    }

    @XmlRootElement
    @XmlType
    @XmlAccessorType(XmlAccessType.PROPERTY)
    class WebsocketSessionDoesNotExistException extends PreconditionViolation
    {
        public WebsocketSessionDoesNotExistException() {}

        public WebsocketSessionDoesNotExistException(String message)
        {
            super(message);
        }

        public WebsocketSessionDoesNotExistException(String message, Throwable cause)
        {
            super(message, cause);
        }

        public WebsocketSessionDoesNotExistException(Throwable cause)
        {
            super(cause);
        }
    }

    interface WebsocketSessionPersistenceManagerLocal extends WebsocketSessionPersistenceManager{}
    interface WebsocketSessionPersistenceManagerRemote extends WebsocketSessionPersistenceManager{}
}
