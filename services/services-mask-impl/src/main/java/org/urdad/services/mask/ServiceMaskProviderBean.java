package org.urdad.services.mask;

import org.urdad.services.RequestNotValidException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class ServiceMaskProviderBean implements ServiceMaskProvider.ServiceMaskProviderLocal, ServiceMaskProvider.ServiceMaskProviderRemote {
    @Override
    public provideServiceMaskResponse provideServiceMask(provideServiceMaskRequest provideServiceMaskRequest) throws RequestNotValidException {
        return new provideServiceMaskResponse(serviceMaskMap.get(provideServiceMaskRequest.getServiceMaskIdentifier()));
    }

    @Override
    public Future<provideServiceMaskResponse> provideServiceMaskAsync(provideServiceMaskRequest provideServiceMaskRequest) throws RequestNotValidException {
        throw new UnsupportedOperationException("Only synchronous invocation supported at this time.");
    }

    public ServiceMaskProviderBean(Map<String, ServiceMask> serviceMaskMap) {
        this.serviceMaskMap.putAll(serviceMaskMap);
    }

    private Map<String, ServiceMask> serviceMaskMap = new HashMap<>();
}
