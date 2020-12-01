package org.urdad.jaxrs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.jaxrs.adapter.rpcrest.CustomHttpHeaders;
import org.urdad.proxy.websocket.CustomWebsocketHeaders;
import org.urdad.proxy.websocket.WebsocketSessionProvider;
import org.urdad.services.RequestNotValidException;

import javax.inject.Inject;
import javax.websocket.Session;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Provider
public class WebsocketSessionIdHeaderSetter implements WriterInterceptor
{

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        Session session = null;
        try {
            session = websocketSessionProvider.provideSession(new WebsocketSessionProvider.ProvideSessionRequest()).getSession();
        } catch (RequestNotValidException e) {
            logger.error("Error setting Rest websocket id Header: {}", e);
        }
        if (session != null) {
            logger.debug("Websocket session with ID {} found.", session.getId());
            context.getHeaders().add(CustomHttpHeaders.X_WEBSOCKET_SESSION_IDENTIFIER, Arrays.asList(session.getId()));
        } else {
            logger.debug("No websocket session found.");
        }

        context.proceed();
    }

    private static final Logger logger = LoggerFactory.getLogger(WebsocketSessionIdHeaderSetter.class);

    @Inject
    private WebsocketSessionProvider.WebsocketSessionProviderLocal websocketSessionProvider;

}