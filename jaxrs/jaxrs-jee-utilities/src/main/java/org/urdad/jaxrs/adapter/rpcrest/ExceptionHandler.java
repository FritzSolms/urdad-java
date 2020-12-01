package org.urdad.jaxrs.adapter.rpcrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.ejb.EJBAccessException;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.services.PreconditionViolation;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.ServiceUsageNotAuthorisedError;
import org.urdad.services.UnexpectedServiceError;

@Provider
public class ExceptionHandler implements ExceptionMapper<Throwable>
{
	@Override
	public Response toResponse(Throwable throwable)
	{
		Response.Status status;
		Map<String, String> headers = new TreeMap<>();

		if (throwable instanceof RequestNotValidException)
		{
			status = Response.Status.BAD_REQUEST;
		}
		else if (throwable instanceof PreconditionViolation)
		{
			status = Response.Status.FORBIDDEN;
		}
		else if (throwable instanceof EJBAccessException)
		{
			logger.error(throwable.getMessage(), throwable);

			throwable = new ServiceUsageNotAuthorisedError(throwable.getMessage());
			status = Response.Status.UNAUTHORIZED;
			headers.put(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"" + securityRealmName + "\"");
		}
		else
		{
			logger.error(throwable.getMessage(), throwable);

			throwable = new UnexpectedServiceError(throwable.getMessage());
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}

		headers.put(CustomHttpHeaders.X_THROWABLE_TYPE, throwable.getClass().getName());

		String acceptHeader = httpHeaders.getHeaderString(HttpHeaders.ACCEPT);

		String entity;

		if ((Strings.isNullOrEmpty(acceptHeader))
				|| ((!acceptHeader.equals(MediaType.APPLICATION_XML)) && (!acceptHeader.equals(MediaType.APPLICATION_JSON))))
		{
			headers.put(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);
			entity = throwable.getMessage();
		}
		else if (acceptHeader.equals(MediaType.APPLICATION_XML))
		{
			headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);
			entity = convertThrowableToXml(throwable);
		}
		else
		{
			headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
			entity = convertThrowableToJson(throwable);
		}

		Response.ResponseBuilder responseBuilder = Response.status(status).entity(entity);

		headers.entrySet().forEach(header -> responseBuilder.header(header.getKey(), header.getValue()));

		return responseBuilder.build();
	}

	private String convertThrowableToXml(Throwable throwable)
	{
		String xml;

		try
		{
			xml = jaxbMarshaller.marshallJavaObjectToXml(
					new org.urdad.xml.binding.Marshaller.MarshallJavaObjectToXmlRequest(throwable, throwable.getClass())).getXml();
		} catch (RequestNotValidException | org.urdad.xml.binding.Marshaller.JavaObjectOrBoundClassesNotValidException e)
		{
			throw new RuntimeException(e);
		}

		// Remove empty stacktrace elements.
		xml = xml.replaceAll("<stackTrace/>", "");

		if (xml.contains("stackTrace"))
		{
			if (xml.contains("<stackTrace>"))
			{
				Pattern pattern = Pattern.compile("<stackTrace>(.+?)</stackTrace>");

				Matcher matcher = pattern.matcher(xml);

				for (int i = 0; i < matcher.groupCount(); i++)
				{
					xml = xml.replace(matcher.group(i), "");
				}
			}
		}

		return xml;
	}

	private String convertThrowableToJson(Throwable throwable)
	{
		String json;

		// Use Jackson to remove unwanted elements.
		ObjectMapper objectMapper = new ObjectMapper();

		try
		{
			ObjectNode rootNode = (ObjectNode) objectMapper.readTree(objectMapper.writeValueAsString(throwable));

			rootNode.remove("cause");
			rootNode.remove("stackTrace");
			rootNode.remove("localizedMessage");
			rootNode.remove("suppressed");

			json = objectMapper.writeValueAsString(rootNode);

		} catch (IOException e)
		{
			// System error. Should not happen.
			throw new RuntimeException(e);
		}

		return json;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

	@Context
	private HttpHeaders httpHeaders;
	@Resource(name = "java:app/env/securityRealmName")
	private String securityRealmName;
	@Inject
	private org.urdad.xml.binding.Marshaller.MarshallerLocal jaxbMarshaller;
}