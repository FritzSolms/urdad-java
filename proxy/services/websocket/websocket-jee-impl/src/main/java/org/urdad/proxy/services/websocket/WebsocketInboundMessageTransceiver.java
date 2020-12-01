package org.urdad.proxy.services.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.proxy.websocket.*;
import org.urdad.services.*;
import org.urdad.services.mask.ServiceMask;
import org.urdad.services.mask.ServiceMaskProvider;
import org.urdad.validation.services.ServiceValidationUtilities;

import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;

/*
This needs to be defined in the war package to work.
So create a class that extends this class in your assembly war.
 */

@ServerEndpoint("/websocket/services")
public class WebsocketInboundMessageTransceiver {

    @OnOpen
    public void onOpen(Session session) {
        try {
            websocketSessionPersistenceManager.insertWebsocketSession(new WebsocketSessionPersistenceManager.InsertWebsocketSessionRequest(session));

            handleConnect(session);


        } catch (RequestNotValidException e) {
            throw new RuntimeException("Unexpected Request Not valid in org.urdad.proxy.services.websocket.WebsocketInboundMessageTransceiver.onOpen.");
        } catch (WebsocketSessionPersistenceManager.WebsocketSessionAlreadytExistsException e) {
            throw new RuntimeException("Unexpected Websocket Already Exists in org.urdad.proxy.services.websocket.WebsocketInboundMessageTransceiver.onOpen.");
        }
    }

    @OnClose
    public void onClose(Session session) {
        try {
            websocketSessionPersistenceManager.removeWebsocketSession(new WebsocketSessionPersistenceManager.RemoveWebsocketSessionRequest(session));
        } catch (RequestNotValidException e) {
            throw new RuntimeException("Unexpected Request Not valid in org.urdad.proxy.services.websocket.WebsocketInboundMessageTransceiver.onClose.");

        } catch (WebsocketSessionPersistenceManager.WebsocketSessionDoesNotExistException e) {
            logger.trace("Session not in persistence manager.");
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        //decode
        WebsocketMessage inboundWebsocketMessage = decodeWebsocketMessage(message);
//        try {
//            websocketSessionProvider.setSession(new WebsocketSessionProvider.SetSessionRequest(session));
//        } catch (RequestNotValidException e) {
//            throw new RuntimeException(e);
//        }

        if ("true".equalsIgnoreCase(inboundWebsocketMessage.getHeaders().getProperty(CustomWebsocketHeaders.WS_SERVICE_RESULT))) {
            //handle response
            handleResult(inboundWebsocketMessage);
        } else {
            //handle request
            throw new UnsupportedOperationException("Can only handle service invocation results");
//            handleRequest(inboundWebsocketMessage, session);
        }
    }

    @OnError
    public void onError(Throwable t) {
        logger.error("Error during websocket: {}", t);
    }

    //    private void handleRequest(WebsocketMessage inboundWebsocketMessage, Session session) {
//        //invoke service
//        Object response = invokeService(inboundWebsocketMessage);
//
//        //encode
//        WebsocketMessage outboundWebsocketMessage = new WebsocketMessage();
//        outboundWebsocketMessage.setPayload(response);
//        outboundWebsocketMessage.getHeaders().setProperty(CustomWebsocketHeaders.WS_REQUEST_IDENTIFIER, inboundWebsocketMessage.getHeaders().getProperty(CustomWebsocketHeaders.WS_REQUEST_IDENTIFIER));
//        outboundWebsocketMessage.getHeaders().setProperty(CustomWebsocketHeaders.WS_SERVICE_PROVIDER_TYPE, inboundWebsocketMessage.getHeaders().getProperty(CustomWebsocketHeaders.WS_SERVICE_PROVIDER_TYPE));
//        outboundWebsocketMessage.getHeaders().setProperty(CustomWebsocketHeaders.WS_PAYLOAD_TYPE, response.getClass().getName());
//        outboundWebsocketMessage.getHeaders().setProperty(CustomWebsocketHeaders.WS_SERVICE_RESULT, "true");
//
//        String encodedMessage = encodeWebsocketMessage(outboundWebsocketMessage);
//        try {
//            session.getBasicRemote().sendText(encodedMessage);
//        } catch (IOException e) {
//            throw new RuntimeException("Error sending websocket result.", e);
//        }
//    }
    private void handleConnect(Session session) {

        //encode bootstrap message
        WebsocketMessage outboundWebsocketMessage = new WebsocketMessage();
        outboundWebsocketMessage.setPayload(new WebsocketBootstrap());
        outboundWebsocketMessage.getHeaders().setProperty(CustomWebsocketHeaders.WS_PAYLOAD_TYPE, WebsocketBootstrap.class.getName());
        outboundWebsocketMessage.getHeaders().setProperty(CustomWebsocketHeaders.WS_WEBSOCKET_SESSION_IDENTIFIER, session.getId());

        String encodedMessage = encodeWebsocketMessage(outboundWebsocketMessage);
        try {
            session.getBasicRemote().sendText(encodedMessage);
        } catch (IOException e) {
            throw new RuntimeException("Error sending websocket result.", e);
        }
    }

    private void handleResult(WebsocketMessage inboundWebsocketMessage) {
        ServiceInvocationResponsePersistenceManager.FindServiceInvocationResponseResponse serviceInvocationResponse;
        try {
            serviceInvocationResponse = serviceInvocationResponsePersistenceManager.findServiceInvocationResponse(new ServiceInvocationResponsePersistenceManager.FindServiceInvocationResponseRequest(new RequestIdentifierCriteria(inboundWebsocketMessage.getHeaders().getProperty(CustomWebsocketHeaders.WS_REQUEST_IDENTIFIER))));
        } catch (RequestNotValidException e) {
            throw new RuntimeException("Error handling websocket result.", e);
        }
        if (inboundWebsocketMessage.getPayload() instanceof Response) {
            serviceInvocationResponse.getFuture().complete((Response) inboundWebsocketMessage.getPayload());
        } else if (inboundWebsocketMessage.getPayload() instanceof Throwable) {
            serviceInvocationResponse.getFuture().completeExceptionally((Throwable) inboundWebsocketMessage.getPayload());
        } else {
            throw new RuntimeException("Unexpected Payload, bust be Response or Throwable, was: " + inboundWebsocketMessage.getPayload().getClass().getName());
        }
    }

    private WebsocketMessage decodeWebsocketMessage(String message) {
        WebsocketMessage websocketMessage;
        //transform wrapper
        try {
            websocketMessage = (WebsocketMessage) configuration.getInboundPayloadTransformer().transformPayload(new PayloadTransformer.TransformPayloadRequest(message, WebsocketMessage.class)).getPayload();
        } catch (RequestNotValidException | PayloadTransformer.PayloadMustBeAmenableToTransformationException | ClassCastException e) {
            throw new RuntimeException("Error handling websocket payload.", e);
        }

        //decompress
        if (configuration.getPayloadDecompressor() != null) {
            try {
                websocketMessage.setPayload(configuration.getPayloadDecompressor().decompressPayload(new PayloadDecompressor.DecompressPayloadRequest((byte[]) websocketMessage.getPayload())).getPayload());
            } catch (RequestNotValidException | ClassCastException e) {
                throw new RuntimeException("Error handling websocket payload.", e);
            }
        }
        //payload transform
        for (Map.Entry<Object, Object> objectObjectEntry : websocketMessage.getHeaders().entrySet()) {
            logger.info("Header: {}, Value: {}", objectObjectEntry.getKey(), objectObjectEntry.getValue());
        }
        Class targetClass = null;
        String className = websocketMessage.getHeaders().getProperty(CustomWebsocketHeaders.WS_PAYLOAD_TYPE);
        logger.info("Payload class specified in head: {}", className);
        if (className != null) {
            for (Class aClass : classesToBeBoundJson.getClassesToBeBound()) {
                if (className.equalsIgnoreCase(aClass.getName())) {
                    targetClass = aClass;
                    break;
                }
            }
        }
        if (targetClass == null && className != null) {
            for (Class aClass : classesToBeBoundXml.getClassesToBeBound()) {
                if (className.equalsIgnoreCase(aClass.getName())) {
                    targetClass = aClass;
                    break;
                }
            }
        }
        try {
            logger.info("Transforming payload to class: {}", targetClass);
            websocketMessage.setPayload(configuration.getInboundPayloadTransformer().transformPayload(new PayloadTransformer.TransformPayloadRequest(websocketMessage.getPayload(), targetClass)).getPayload());
        } catch (RequestNotValidException | PayloadTransformer.PayloadMustBeAmenableToTransformationException e) {
            throw new RuntimeException("Error handling websocket payload.", e);
        }
        return websocketMessage;
    }

    private String encodeWebsocketMessage(WebsocketMessage outboundWebsocketMessage) {
        //payload transform
        try {
            outboundWebsocketMessage.setPayload(configuration.getOutboundPayloadTransformer().transformPayload(new PayloadTransformer.TransformPayloadRequest(outboundWebsocketMessage.getPayload())).getPayload());
        } catch (RequestNotValidException | PayloadTransformer.PayloadMustBeAmenableToTransformationException e) {
            throw new RuntimeException("Error encoding websocket payload.", e);
        }
        //compress
        if (configuration.getPayloadCompressor() != null) {
            try {
                outboundWebsocketMessage.setPayload(configuration.getPayloadCompressor().compressPayload(new PayloadCompressor.CompressPayloadRequest(outboundWebsocketMessage.getPayload())).getCompressedPayload());
            } catch (RequestNotValidException e) {
                throw new RuntimeException("Error encoding websocket payload.", e);
            }

        }
        //transform wrapper
        try {
            return (String) configuration.getOutboundPayloadTransformer().transformPayload(new PayloadTransformer.TransformPayloadRequest(outboundWebsocketMessage)).getPayload();
        } catch (RequestNotValidException | PayloadTransformer.PayloadMustBeAmenableToTransformationException | ClassCastException e) {
            throw new RuntimeException("Error encoding websocket payload.", e);
        }
    }

//    private Object invokeService(WebsocketMessage inboundWebsocketMessage) {
//        ServiceMask serviceMask;
//        try {
//            serviceMask = serviceMaskProvider.provideServiceMask(new ServiceMaskProvider.provideServiceMaskRequest(inboundWebsocketMessage.getHeaders().getProperty(CustomWebsocketHeaders.WS_SERVICE_PROVIDER_TYPE))).getServiceMask();
//        } catch (RequestNotValidException e) {
//            throw new RuntimeException("Error invoking service.", e);
//        }
//        try {
//            return serviceMask.invokeService((Request) inboundWebsocketMessage.getPayload());
//        } catch (Throwable throwable) {
//            if (throwable instanceof PreconditionViolation) {
//                return throwable;
//            } else {
//                throw new RuntimeException("Error invoking service.", throwable);
//            }
//        }
//    }

    private static final Logger logger = LoggerFactory.getLogger(org.urdad.proxy.services.websocket.WebsocketInboundMessageTransceiver.class);

//    @Inject
//    private WebsocketSessionProvider.WebsocketSessionProviderLocal websocketSessionProvider;

    @Inject
    private WebsocketSessionPersistenceManager.WebsocketSessionPersistenceManagerLocal websocketSessionPersistenceManager;

    @Inject
    private WebsocketEndpointConfiguration configuration;

//    @Inject
//    private ServiceMaskProvider.ServiceMaskProviderLocal serviceMaskProvider;

    @Inject
    private ServiceInvocationResponsePersistenceManager.ServiceInvocationResponsePersistenceManagerLocal serviceInvocationResponsePersistenceManager;

    @Inject
    private ServiceValidationUtilities serviceValidationUtilities;

    @Inject
    private org.urdad.json.binding.ClassesToBeBound classesToBeBoundJson;
    @Inject
    private org.urdad.xml.binding.ClassesToBeBound classesToBeBoundXml;
}
