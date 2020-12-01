/*
 * Copyright 2019 Dr. Fritz Solms & Craig Edwards
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.urdad.xml.binding;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;
import org.urdad.xml.binding.Unmarshaller.UnmarshallXmlToJavaObjectResponse;

/**
 * FIXME: Javadoc
 */
@Service
public class UnmarshallerBean implements Unmarshaller.UnmarshallerLocal, Unmarshaller.UnmarshallerRemote
{

	/** FIXME: Javadoc */
	@Override
	@SuppressWarnings("unchecked")
	public UnmarshallXmlToJavaObjectResponse unmarshallXmlToJavaObject(UnmarshallXmlToJavaObjectRequest unmarshallXmlToJavaObjectRequest)
			throws RequestNotValidException, XmlOrBoundClassesNotValidException
	{
		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(UnmarshallXmlToJavaObjectRequest.class, unmarshallXmlToJavaObjectRequest);

		try
		{
			String searchString = "encoding=\"";
			int startIndex = unmarshallXmlToJavaObjectRequest.getXml().indexOf(searchString) + searchString.length();
			int endindex = unmarshallXmlToJavaObjectRequest.getXml().indexOf("\"", startIndex);

			String characterEncoding;
			if (startIndex != -1 && endindex != -1)
			{
				characterEncoding = unmarshallXmlToJavaObjectRequest.getXml().substring(startIndex, endindex);
			}
			else
			{
				characterEncoding = defaultCharacterEncoding;
			}
			return new UnmarshallXmlToJavaObjectResponse(
					JAXBContext.newInstance(unmarshallXmlToJavaObjectRequest.getClassesToBeBound()).createUnmarshaller()
							.unmarshal(new ByteArrayInputStream(unmarshallXmlToJavaObjectRequest.getXml().getBytes(characterEncoding))));
		} catch (JAXBException e)
		{
			logger.error("Unable to marshall the specified XML representation to its corresponding Java " + "representation.", e);

			// Check pre-condition: XML and bound class(es) must be valid.
			throw new XmlOrBoundClassesNotValidException(e);
		} catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static final String defaultCharacterEncoding = "UTF-8";
	private static final Logger logger = LoggerFactory.getLogger(MarshallerBean.class);
	@Inject
	private ServiceValidationUtilities serviceValidationUtilities;

}