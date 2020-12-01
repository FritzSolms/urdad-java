package org.urdad.jaxrs.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.urdad.services.RequestNotValidException;
import org.urdad.xml.binding.ClassesToBeBound;
import org.urdad.xml.binding.Marshaller;
import org.urdad.xml.binding.Unmarshaller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A custom spring xml converter for rest queries. This converter uses specified org.urdad.xml.binding.Marshaller and
 * org.urdad.xml.binding.Unmarshaller implementations to do the xml conversions.
 */
public class XmlConverter implements HttpMessageConverter
{
	/**
	 * Default constructor for completion purposes, but should not be used from outside.
	 */
	private XmlConverter()
	{
		super();
	}

	/**
	 * Constructor to represent how this object should be configured.
	 *
	 * @param marshaller       The xml marshaller to be used to marshall the java object to xml text.
	 * @param unmarshaller     The xml unmarshaller to be used to unmarshall the xml text to a java object.
	 * @param classesToBeBound The classes that can be marshalled and unmarshalled. This must be specified since
	 *                         if there are subtypes they need to be understood.
	 */
	public XmlConverter(Marshaller.MarshallerLocal marshaller, Unmarshaller.UnmarshallerLocal unmarshaller,
			ClassesToBeBound classesToBeBound)
	{
		this();
		this.marshaller = marshaller;
		this.unmarshaller = unmarshaller;
		this.classesToBeBound = classesToBeBound;
	}

	@Override
	public boolean canRead(Class aClass, MediaType mediaType)
	{
		if (!mediaType.equals(MediaType.APPLICATION_XML))
		{
			return false;
		}

		return true;
	}

	@Override
	public boolean canWrite(Class aClass, MediaType mediaType)
	{
		if (!mediaType.equals(MediaType.APPLICATION_XML))
		{
			return false;
		}

		return true;
	}

	@Override
	public List<MediaType> getSupportedMediaTypes()
	{
		List<MediaType> mediaTypes = new ArrayList<>();
		mediaTypes.add(MediaType.APPLICATION_XML);
		return mediaTypes;
	}

	@Override
	public Object read(Class aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException
	{
		logger.trace("Converting xml text to a java object of type {}.", aClass.getName());

		// Read the message as a string value from the http input message.
		StringBuilder stringBuilder = new StringBuilder();
		String inputMessage;
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpInputMessage.getBody())))
		{
			while ((inputMessage = bufferedReader.readLine()) != null)
			{
				stringBuilder.append(inputMessage);
			}
		} catch (IOException e)
		{
			throw new RuntimeException("Could not unmarshall xml to java object due to: " + e.getMessage(), e);
		}

		// Convert the string value which is expected to be valid xml, to a java object.
		Object convertedObject;
		try
		{
			convertedObject = unmarshaller.unmarshallXmlToJavaObject(
					new Unmarshaller.UnmarshallXmlToJavaObjectRequest(stringBuilder.toString(), classesToBeBound.getClassesToBeBound()))
					.getJavaObject();
		} catch (RequestNotValidException | Unmarshaller.XmlOrBoundClassesNotValidException e)
		{
			throw new RuntimeException("Could not unmarshall xml to java object due to: " + e.getMessage(), e);
		}

		logger.trace("Converted java object: {}: {}", convertedObject.getClass().getName(), convertedObject);

		return convertedObject;
	}

	@Override
	public void write(Object o, MediaType mediaType, HttpOutputMessage httpOutputMessage)
			throws IOException, HttpMessageNotWritableException
	{
		logger.trace("Converting java object of type {} to xml.", o.getClass());

		String convertedValue;
		if (o instanceof String)
		{
			// Send as is, do not convert.
			convertedValue = (String) o;
		}
		else
		{
			// Convert to xml text.
			try
			{
				convertedValue = marshaller
						.marshallJavaObjectToXml(new Marshaller.MarshallJavaObjectToXmlRequest(o, classesToBeBound.getClassesToBeBound()))
						.getXml();

			} catch (RequestNotValidException | Marshaller.JavaObjectOrBoundClassesNotValidException e)
			{
				throw new RuntimeException("Could not marshall java object to xml due to: " + e.getMessage(), e);
			}
		}

		logger.trace("Converted xml value: {}", convertedValue);

		// Write converted value to the http output message.
		httpOutputMessage.getBody().write(convertedValue.getBytes());
		httpOutputMessage.getBody().flush();
	}

	private static final Logger logger = LoggerFactory.getLogger(XmlConverter.class);
	private Marshaller.MarshallerLocal marshaller;
	private Unmarshaller.UnmarshallerLocal unmarshaller;
	private ClassesToBeBound classesToBeBound;
}
