package org.urdad.proxy.services.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.proxy.websocket.*;
import org.urdad.services.InvokedServiceFuture;
import org.urdad.services.Request;
import org.urdad.services.Response;
import org.urdad.services.ServiceInvocationResponsePersistenceManager;
import org.urdad.services.mask.ServiceMask;
import org.urdad.validation.services.ServiceValidationUtilities;

import javax.inject.Inject;
import javax.websocket.Session;
import java.util.UUID;
import java.util.concurrent.Future;

public class WebsocketOutboundMessageTransceiver implements ServiceMask {
    @Override
    public Response invokeService(Request request) throws Throwable {
        return invokeServiceAsync(request).get();
    }

    @Override
    public Future<? extends Response> invokeServiceAsync(Request request) throws Throwable {
        Session session = websocketSessionProvider.provideSession(new WebsocketSessionProvider.ProvideSessionRequest()).getSession();

        if(session == null){
            throw new RuntimeException("No Websocket session available to communicate on.");
        }
        String requestId = UUID.randomUUID().toString();
        WebsocketMessage websocketMessage = new WebsocketMessage();
        //TODO set headers
        websocketMessage.getHeaders().setProperty(CustomWebsocketHeaders.WS_REQUEST_IDENTIFIER, requestId);
        websocketMessage.getHeaders().setProperty(CustomWebsocketHeaders.WS_SERVICE_PROVIDER_TYPE, serviceProviderType.getName());
        websocketMessage.getHeaders().setProperty(CustomWebsocketHeaders.WS_PAYLOAD_TYPE, request.getClass().getName());

        //transform request message
        Object transformedRequest = configuration.getRequestPayloadTransformer().transformPayload(new PayloadTransformer.TransformPayloadRequest(request)).getPayload();

        //compress request message
        if (configuration.getPayloadCompressor() != null) {
            transformedRequest = configuration.getPayloadCompressor().compressPayload(new PayloadCompressor.CompressPayloadRequest(transformedRequest)).getCompressedPayload();
        }

        //set message payload
        websocketMessage.setPayload(transformedRequest);

        //transform websocket message
        Object transformedMessage = configuration.getRequestPayloadTransformer().transformPayload(new PayloadTransformer.TransformPayloadRequest(websocketMessage)).getPayload();
        if (!(transformedMessage instanceof String)) {
            throw new RuntimeException("Payload was not transformed to String, please check WebsocketOutboundMessageTranceiverConfiguration.");
        }

        //create response future
        InvokedServiceFuture invokedServiceFuture = new InvokedServiceFuture();
        configuration.getServiceInvocationResponsePersistenceManager().insertServiceInvocationResponse(new ServiceInvocationResponsePersistenceManager.InsertServiceInvocationResponseRequest(requestId, invokedServiceFuture));

        //send message
        logger.info("Sending message over session: {}",session.getId());
        session.getBasicRemote().sendText((String) transformedMessage);

        return invokedServiceFuture;
    }

    @Override
    public RetrieveServiceProviderTypeResponse retrieveServiceProviderType(RetrieveServiceProviderTypeRequest retrieveServiceProviderTypeRequest) {
        return new RetrieveServiceProviderTypeResponse(serviceProviderType);
    }

    public WebsocketOutboundMessageTransceiver(Class serviceProviderType, WebsocketSessionProvider.WebsocketSessionProviderLocal websocketSessionProvider, WebsocketOutboundMessageTranceiverConfiguration configuration) {
        this.websocketSessionProvider = websocketSessionProvider;
        this.configuration = configuration;
        this.serviceProviderType = serviceProviderType;
    }

    private static final Logger logger = LoggerFactory.getLogger(WebsocketOutboundMessageTransceiver.class);


    private WebsocketSessionProvider.WebsocketSessionProviderLocal websocketSessionProvider;


    private WebsocketOutboundMessageTranceiverConfiguration configuration;


    private Class serviceProviderType;

}
