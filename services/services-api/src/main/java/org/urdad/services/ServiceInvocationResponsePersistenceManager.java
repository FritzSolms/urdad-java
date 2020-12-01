package org.urdad.services;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.concurrent.Future;

public interface ServiceInvocationResponsePersistenceManager {

    /**
     * TODO Javadoc
     */
    public FindServiceInvocationResponseResponse findServiceInvocationResponse(FindServiceInvocationResponseRequest findServiceInvocationResponseRequest) throws RequestNotValidException;

    public Future<FindServiceInvocationResponseResponse> findServiceInvocationResponseAsync(FindServiceInvocationResponseRequest findServiceInvocationResponseRequest) throws RequestNotValidException;

    class FindServiceInvocationResponseRequest extends Request {
        @NotNull(message = "A criteria must be specified.")
        private Criteria criteria;

        public Criteria getCriteria() {
            return criteria;
        }

        public void setCriteria(Criteria criteria) {
            this.criteria = criteria;
        }

        public FindServiceInvocationResponseRequest() {
        }

        public FindServiceInvocationResponseRequest(Criteria criteria) {

            this.criteria = criteria;
        }
    }

    class FindServiceInvocationResponseResponse extends Response {
        private InvokedServiceFuture future;

        public InvokedServiceFuture getFuture() {
            return future;
        }

        public void setFuture(InvokedServiceFuture future) {
            this.future = future;
        }

        public FindServiceInvocationResponseResponse() {
        }

        public FindServiceInvocationResponseResponse(InvokedServiceFuture future) {

            this.future = future;
        }
    }

    /**
     * TODO Javadoc
     */
    public RemoveServiceInvocationResponseResponse removeServiceInvocationResponse(RemoveServiceInvocationResponseRequest removeServiceInvocationResponseRequest) throws RequestNotValidException, ServiceInvocationResponseDoesNotExistException;

    public Future<RemoveServiceInvocationResponseResponse> removeServiceInvocationResponseAsync(RemoveServiceInvocationResponseRequest removeServiceInvocationResponseRequest) throws RequestNotValidException, ServiceInvocationResponseDoesNotExistException;

    class RemoveServiceInvocationResponseRequest extends Request {
        @NotNull(message = "A criteria must be specified.")
        private Criteria criteria;

        public Criteria getCriteria() {
            return criteria;
        }

        public void setCriteria(Criteria criteria) {
            this.criteria = criteria;
        }

        public RemoveServiceInvocationResponseRequest() {
        }

        public RemoveServiceInvocationResponseRequest(Criteria criteria) {

            this.criteria = criteria;
        }
    }

    class RemoveServiceInvocationResponseResponse extends Response {

    }

    /**
     * TODO Javadoc
     */
    public InsertServiceInvocationResponseResponse insertServiceInvocationResponse(InsertServiceInvocationResponseRequest insertServiceInvocationResponseRequest) throws RequestNotValidException, ServiceInvocationResponseAlreadytExistsException;

    public Future<InsertServiceInvocationResponseResponse> insertServiceInvocationResponseAsync(InsertServiceInvocationResponseRequest insertServiceInvocationResponseRequest) throws RequestNotValidException, ServiceInvocationResponseAlreadytExistsException;

    class InsertServiceInvocationResponseRequest extends Request {
        @NotNull
        @Size(min=1)
        private String requestIdentifier;
        @NotNull
        private InvokedServiceFuture future;

        public String getRequestIdentifier() {
            return requestIdentifier;
        }

        public void setRequestIdentifier(String requestIdentifier) {
            this.requestIdentifier = requestIdentifier;
        }

        public InvokedServiceFuture getFuture() {
            return future;
        }

        public void setFuture(InvokedServiceFuture future) {
            this.future = future;
        }

        public InsertServiceInvocationResponseRequest(String requestIdentifier, InvokedServiceFuture future) {
            this.requestIdentifier = requestIdentifier;
            this.future = future;
        }

        public InsertServiceInvocationResponseRequest() {
        }
    }

    class InsertServiceInvocationResponseResponse extends Response {

    }

    @XmlRootElement
    @XmlType
    @XmlAccessorType(XmlAccessType.PROPERTY)
    class ServiceInvocationResponseAlreadytExistsException extends PreconditionViolation {
        public ServiceInvocationResponseAlreadytExistsException() {
        }

        public ServiceInvocationResponseAlreadytExistsException(String message) {
            super(message);
        }

        public ServiceInvocationResponseAlreadytExistsException(String message, Throwable cause) {
            super(message, cause);
        }

        public ServiceInvocationResponseAlreadytExistsException(Throwable cause) {
            super(cause);
        }
    }

    @XmlRootElement
    @XmlType
    @XmlAccessorType(XmlAccessType.PROPERTY)
    class ServiceInvocationResponseDoesNotExistException extends PreconditionViolation {
        public ServiceInvocationResponseDoesNotExistException() {
        }

        public ServiceInvocationResponseDoesNotExistException(String message) {
            super(message);
        }

        public ServiceInvocationResponseDoesNotExistException(String message, Throwable cause) {
            super(message, cause);
        }

        public ServiceInvocationResponseDoesNotExistException(Throwable cause) {
            super(cause);
        }
    }

    interface ServiceInvocationResponsePersistenceManagerLocal extends ServiceInvocationResponsePersistenceManager {
    }

    interface ServiceInvocationResponsePersistenceManagerRemote extends ServiceInvocationResponsePersistenceManager {
    }
}
