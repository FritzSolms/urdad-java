package org.urdad.jaxrs;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ResponseHeaderLogger implements WriterInterceptor
{
	@Override
	public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
	{
		for (Map.Entry<String, List<Object>> stringListEntry : context.getHeaders().entrySet())
		{
			logger.trace(stringListEntry.getKey() + " : " + stringListEntry.getValue());
		}

		context.proceed();
	}

	private static final Logger logger = LoggerFactory.getLogger(ResponseHeaderLogger.class);
}