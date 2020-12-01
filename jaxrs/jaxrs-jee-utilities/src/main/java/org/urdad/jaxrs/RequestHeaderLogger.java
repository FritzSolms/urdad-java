package org.urdad.jaxrs;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class RequestHeaderLogger implements ReaderInterceptor
{
	@Override
	public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException
	{
		for (Map.Entry<String, List<String>> stringListEntry : context.getHeaders().entrySet())
		{
			logger.trace(stringListEntry.getKey() + " : " + stringListEntry.getValue());
		}

		return context.proceed();
	}

	private static final Logger logger = LoggerFactory.getLogger(RequestHeaderLogger.class);
}