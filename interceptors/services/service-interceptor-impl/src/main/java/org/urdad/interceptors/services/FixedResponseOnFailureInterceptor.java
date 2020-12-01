package org.urdad.interceptors.services;

import org.urdad.services.PreconditionViolation;
import org.urdad.services.Request;
import org.urdad.services.Response;
import org.urdad.services.mask.ServiceMask;

import java.util.concurrent.Future;

/**
 * This interceptor is provided a response object. If the wrapped Service Provider Proxy throws any non-PreconditionViolation, the provided response object is returned.
 */
public class FixedResponseOnFailureInterceptor implements ServiceMask.ServiceMaskLocal, ServiceMask.ServiceMaskRemote {
    private ServiceMask next;
    private Response fixedResponse;

    public FixedResponseOnFailureInterceptor(ServiceMask next, Response fixedResponse) {
        this.next = next;
        this.fixedResponse = fixedResponse;
    }

    @Override
    public Response invokeService(Request request) throws Throwable {
        try {
            return next.invokeService(request);
        } catch (Throwable t) {
            if (t instanceof PreconditionViolation) {
                throw t;
            } else {
                return fixedResponse;
            }
        }
    }

    @Override
    public Future<Response> invokeServiceAsync(Request request) throws Throwable {
        throw new UnsupportedOperationException("FixedResponseOnFailureInterceptor does not support async invocation");
    }

    @Override
    public RetrieveServiceProviderTypeResponse retrieveServiceProviderType(RetrieveServiceProviderTypeRequest retrieveServiceProviderTypeRequest) {
        return next.retrieveServiceProviderType(retrieveServiceProviderTypeRequest);
    }
}
