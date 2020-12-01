package org.urdad.jaxrs;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.websocket.Session;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.jaxrs.adapter.rpcrest.CustomHttpHeaders;
import org.urdad.proxy.websocket.WebsocketSessionPersistenceManager;
import org.urdad.proxy.websocket.WebsocketSessionProvider;
import org.urdad.services.RequestNotValidException;

@Provider
public class WebsocketSessionSetter implements ReaderInterceptor
{
	@Override
	public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException
	{
		for (Map.Entry<String, List<String>> stringListEntry : context.getHeaders().entrySet())
		{
			if(stringListEntry.getKey().equalsIgnoreCase(CustomHttpHeaders.X_WEBSOCKET_SESSION_IDENTIFIER)){
				for (String sessionId : stringListEntry.getValue()) {

					try {
						Session websocketSession = websocketSessionPersistenceManager.findWebsocketSession(new WebsocketSessionPersistenceManager.FindWebsocketSessionRequest(sessionId)).getSession();

						if(websocketSession!= null){

							logger.debug("Websocket session with ID {} found.",websocketSession.getId());
							websocketSessionProvider.setSession(new WebsocketSessionProvider.SetSessionRequest(websocketSession));
							return context.proceed();
						} else {
							logger.debug("No websocket session found for ID {}.",sessionId);
						}
					} catch (RequestNotValidException e) {
						logger.error("Error processing Rest Headers: {}",e);
					}
				}
			}
		}

		return context.proceed();
	}

	private static final Logger logger = LoggerFactory.getLogger(WebsocketSessionSetter.class);
	@Inject
	private WebsocketSessionPersistenceManager.WebsocketSessionPersistenceManagerLocal websocketSessionPersistenceManager;
	@Inject
	private WebsocketSessionProvider.WebsocketSessionProviderLocal websocketSessionProvider;

}