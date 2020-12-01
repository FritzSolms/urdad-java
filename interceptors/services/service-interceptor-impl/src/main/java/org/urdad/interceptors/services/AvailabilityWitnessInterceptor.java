package org.urdad.interceptors.services;

import org.urdad.events.EventPublisher;
import org.urdad.events.services.ServiceProviderAvailableEvent;
import org.urdad.events.services.ServiceProviderNotAvailableEvent;
import org.urdad.services.PreconditionViolation;
import org.urdad.services.Request;
import org.urdad.services.Response;
import org.urdad.services.mask.ServiceMask;

import java.util.concurrent.Future;

/**
 * This interceptor is provided a response object. If the wrapped Service Provider Proxy throws any non-PreconditionViolation, the provided response object is returned.
 */
public class AvailabilityWitnessInterceptor implements ServiceMask.ServiceMaskLocal, ServiceMask.ServiceMaskRemote {
    private ServiceMask next;
    private EventPublisher eventPublisher;
    private String serviceProviderIdentifier;

    public AvailabilityWitnessInterceptor(ServiceMask next, EventPublisher eventPublisher) {
        this.next = next;
        this.eventPublisher = eventPublisher;
    }

    public AvailabilityWitnessInterceptor(ServiceMask next, EventPublisher eventPublisher, String serviceProviderIdentifier) {
        this.next = next;
        this.eventPublisher = eventPublisher;
        this.serviceProviderIdentifier = serviceProviderIdentifier;
    }

    @Override
    public Response invokeService(Request request) throws Throwable {
        try {
            Response response = next.invokeService(request);
            eventPublisher.publishEvent(new EventPublisher.PublishEventRequest(new ServiceProviderAvailableEvent(retrieveServiceProviderType(new RetrieveServiceProviderTypeRequest()).getServiceProviderType().getName(), serviceProviderIdentifier)));
            return response;
        } catch (Throwable t) {
            if (t instanceof PreconditionViolation) {
                eventPublisher.publishEvent(new EventPublisher.PublishEventRequest(new ServiceProviderAvailableEvent(retrieveServiceProviderType(new RetrieveServiceProviderTypeRequest()).getServiceProviderType().getName(), serviceProviderIdentifier)));
                throw t;
            } else {
                eventPublisher.publishEvent(new EventPublisher.PublishEventRequest(new ServiceProviderNotAvailableEvent(retrieveServiceProviderType(new RetrieveServiceProviderTypeRequest()).getServiceProviderType().getName(), serviceProviderIdentifier)));
                throw t;
            }
        }
    }

    @Override
    public Future<Response> invokeServiceAsync(Request request) throws Throwable {
        throw new UnsupportedOperationException("AvailabilityWitnessInterceptor does not support async invocation");
    }

    @Override
    public RetrieveServiceProviderTypeResponse retrieveServiceProviderType(RetrieveServiceProviderTypeRequest retrieveServiceProviderTypeRequest) {
        return next.retrieveServiceProviderType(retrieveServiceProviderTypeRequest);
    }
}
