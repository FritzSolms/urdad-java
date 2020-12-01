package org.urdad.json.binding;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Consumes("application/json")
public class CustomMessageBodyReader implements MessageBodyReader
{
	@Override
	public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		return true;
	}

	@Override
	public Object readFrom(Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders,
			InputStream entityStream) throws IOException, WebApplicationException
	{
		return objectMapper.readValue(entityStream, type);
	}

	@PostConstruct
	public void postConstruct()
	{
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JaxbAnnotationModule());
		objectMapper.registerSubtypes(classesToBeBound.getClassesToBeBound());
	}

	private ObjectMapper objectMapper;

	@Inject
	private ClassesToBeBound classesToBeBound;
}