package org.urdad.proxy.services.websocket;

import org.urdad.proxy.websocket.PayloadCompressor;
import org.urdad.proxy.websocket.PayloadTransformer;
import org.urdad.proxy.websocket.WebsocketHeaderSetter;
import org.urdad.services.ServiceInvocationResponsePersistenceManager;
import org.urdad.validation.services.ServiceValidationUtilities;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class WebsocketOutboundMessageTranceiverConfiguration {
    private PayloadCompressor payloadCompressor;
    @NotNull
    private PayloadTransformer requestPayloadTransformer;
    private List<WebsocketHeaderSetter> websocketHeaderSetters = new ArrayList<>();
    private long timeout;
    @NotNull
    private ServiceInvocationResponsePersistenceManager.ServiceInvocationResponsePersistenceManagerLocal serviceInvocationResponsePersistenceManager;
    private ServiceValidationUtilities serviceValidationUtilities;


    public PayloadCompressor getPayloadCompressor() {
        return payloadCompressor;
    }

    public void setPayloadCompressor(PayloadCompressor payloadCompressor) {
        this.payloadCompressor = payloadCompressor;
    }

    public PayloadTransformer getRequestPayloadTransformer() {
        return requestPayloadTransformer;
    }

    public void setRequestPayloadTransformer(PayloadTransformer requestPayloadTransformer) {
        this.requestPayloadTransformer = requestPayloadTransformer;
    }

    public List<WebsocketHeaderSetter> getWebsocketHeaderSetters() {
        return websocketHeaderSetters;
    }

    public void setWebsocketHeaderSetters(List<WebsocketHeaderSetter> websocketHeaderSetters) {
        this.websocketHeaderSetters = websocketHeaderSetters;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public ServiceInvocationResponsePersistenceManager.ServiceInvocationResponsePersistenceManagerLocal getServiceInvocationResponsePersistenceManager() {
        return serviceInvocationResponsePersistenceManager;
    }

    public void setServiceInvocationResponsePersistenceManager(ServiceInvocationResponsePersistenceManager.ServiceInvocationResponsePersistenceManagerLocal serviceInvocationResponsePersistenceManager) {
        this.serviceInvocationResponsePersistenceManager = serviceInvocationResponsePersistenceManager;
    }

    public ServiceValidationUtilities getServiceValidationUtilities() {
        return serviceValidationUtilities;
    }

    public void setServiceValidationUtilities(ServiceValidationUtilities serviceValidationUtilities) {
        this.serviceValidationUtilities = serviceValidationUtilities;
    }
}
