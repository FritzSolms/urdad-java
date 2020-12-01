package org.urdad.jaxrs.adapter.rpcrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.urdad.services.PreconditionViolation;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.UnexpectedServiceError;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class ExceptionHandler
{
	@org.springframework.web.bind.annotation.ExceptionHandler({ Throwable.class })
	public
	@ResponseBody
	ResponseEntity handleThrowable(HttpServletRequest httpServletRequest, Throwable throwable)
	{
		HttpStatus httpStatus;
		MultiValueMap<String, String> headers = new HttpHeaders();

		if (throwable instanceof RequestNotValidException)
		{
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		else if (throwable instanceof PreconditionViolation)
		{
			httpStatus = HttpStatus.FORBIDDEN;
		}
		else
		{
			logger.error(throwable.getMessage(), throwable);

			throwable = new UnexpectedServiceError(throwable.getMessage());
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		headers.put(CustomHttpHeaders.X_THROWABLE_TYPE, Arrays.asList(throwable.getClass().getName()));

		String acceptHeader = httpServletRequest.getHeader(HttpHeaders.ACCEPT);
		String entity;

		if (acceptHeader.equals(MediaType.APPLICATION_XML_VALUE))
		{
			headers.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.APPLICATION_XML_VALUE));
			entity = convertThrowableToXml(throwable);
		}
		else if (acceptHeader.equals(MediaType.APPLICATION_JSON_VALUE))
		{
			headers.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.APPLICATION_JSON_VALUE));
			entity = convertThrowableToJson(throwable);
		}
		else
		{
			headers.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.TEXT_PLAIN_VALUE));
			entity = throwable.getMessage();
		}

		return new ResponseEntity<>(entity, headers, httpStatus);
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

	@Inject
	private org.urdad.xml.binding.Marshaller.MarshallerLocal jaxbMarshaller;
}