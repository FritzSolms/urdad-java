package org.urdad.services;

import org.urdad.validation.services.ServiceValidationUtilities;

import javax.ejb.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Local(ServiceInvocationResponsePersistenceManager.ServiceInvocationResponsePersistenceManagerLocal.class)
@Remote(ServiceInvocationResponsePersistenceManager.ServiceInvocationResponsePersistenceManagerRemote.class)
public class ServiceInvocationResponsePersistenceManagerBean implements ServiceInvocationResponsePersistenceManager.ServiceInvocationResponsePersistenceManagerLocal, ServiceInvocationResponsePersistenceManager.ServiceInvocationResponsePersistenceManagerRemote {

    @Lock(LockType.READ)
    @Override
    public FindServiceInvocationResponseResponse findServiceInvocationResponse(FindServiceInvocationResponseRequest findServiceInvocationResponseRequest) throws RequestNotValidException {
        serviceValidationUtilities.validateRequest(FindServiceInvocationResponseRequest.class, findServiceInvocationResponseRequest);
        Criteria criteria = findServiceInvocationResponseRequest.getCriteria();
        String requestIdentifier = null;
        if (criteria instanceof RequestIdentifierCriteria) {
            requestIdentifier = ((RequestIdentifierCriteria) criteria).getRequestIdentifier();
        } else {
            throw new RequestNotValidException("Only RequestIdentifierCriteria supported, found: " + criteria.getClass().getName());
        }
        return new FindServiceInvocationResponseResponse(futureMap.get(requestIdentifier));
    }

    @Override
    public Future<FindServiceInvocationResponseResponse> findServiceInvocationResponseAsync(FindServiceInvocationResponseRequest findServiceInvocationResponseRequest) throws RequestNotValidException {
        throw new UnsupportedOperationException();
    }

    @Lock(LockType.WRITE)
    @Override
    public RemoveServiceInvocationResponseResponse removeServiceInvocationResponse(RemoveServiceInvocationResponseRequest removeServiceInvocationResponseRequest) throws RequestNotValidException, ServiceInvocationResponseDoesNotExistException {
        serviceValidationUtilities.validateRequest(RemoveServiceInvocationResponseRequest.class, removeServiceInvocationResponseRequest);
        Criteria criteria = removeServiceInvocationResponseRequest.getCriteria();
        String requestIdentifier = null;
        if (criteria instanceof RequestIdentifierCriteria) {
            futureMap.remove(((RequestIdentifierCriteria) criteria).getRequestIdentifier());
        } else {
            throw new RequestNotValidException("Only RequestIdentifierCriteria supported, found: " + criteria.getClass().getName());
        }

        return new RemoveServiceInvocationResponseResponse();
    }

    @Override
    public Future<RemoveServiceInvocationResponseResponse> removeServiceInvocationResponseAsync(RemoveServiceInvocationResponseRequest removeServiceInvocationResponseRequest) throws RequestNotValidException, ServiceInvocationResponseDoesNotExistException {
        throw new UnsupportedOperationException();
    }

    @Lock(LockType.WRITE)
    @Override
    public InsertServiceInvocationResponseResponse insertServiceInvocationResponse(InsertServiceInvocationResponseRequest insertServiceInvocationResponseRequest) throws RequestNotValidException, ServiceInvocationResponseAlreadytExistsException {
        serviceValidationUtilities.validateRequest(InsertServiceInvocationResponseRequest.class, insertServiceInvocationResponseRequest);

        futureMap.put(insertServiceInvocationResponseRequest.getRequestIdentifier(), insertServiceInvocationResponseRequest.getFuture());
        return new InsertServiceInvocationResponseResponse();
    }

    @Override
    public Future<InsertServiceInvocationResponseResponse> insertServiceInvocationResponseAsync(InsertServiceInvocationResponseRequest insertServiceInvocationResponseRequest) throws RequestNotValidException, ServiceInvocationResponseAlreadytExistsException {
        throw new UnsupportedOperationException();
    }

    private Map<String, InvokedServiceFuture> futureMap = new HashMap<>();

    @Inject
    private ServiceValidationUtilities serviceValidationUtilities;
}
