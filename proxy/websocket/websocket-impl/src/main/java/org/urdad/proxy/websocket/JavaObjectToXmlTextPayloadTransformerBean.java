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

package org.urdad.proxy.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;
import org.urdad.xml.binding.ClassesToBeBound;
import org.urdad.xml.binding.Marshaller;
import org.urdad.xml.binding.Marshaller.MarshallerLocal;

/** Transforms Java-based object payloads to their textual XML representations. */
public class JavaObjectToXmlTextPayloadTransformerBean
		implements PayloadTransformer.PayloadTransformerLocal, PayloadTransformer.PayloadTransformerRemote
{

	public JavaObjectToXmlTextPayloadTransformerBean(ServiceValidationUtilities serviceValidationUtilities, MarshallerLocal marshaller,
			ClassesToBeBound classesToBeBound)
	{
		super();
		this.serviceValidationUtilities = serviceValidationUtilities;
		this.marshaller = marshaller;
		this.classesToBeBound = classesToBeBound;
	}

	public JavaObjectToXmlTextPayloadTransformerBean(ServiceValidationUtilities serviceValidationUtilities, MarshallerLocal marshaller,
			ClassesToBeBound classesToBeBound, String characterEncoding)
	{
		super();
		this.serviceValidationUtilities = serviceValidationUtilities;
		this.marshaller = marshaller;
		this.classesToBeBound = classesToBeBound;
		this.characterEncoding = characterEncoding;
	}

	@Override
	public TransformPayloadResponse transformPayload(TransformPayloadRequest transformPayloadRequest)
			throws RequestNotValidException, PayloadTransformer.PayloadMustBeAmenableToTransformationException
	{
		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(TransformPayloadRequest.class, transformPayloadRequest);

		Object javaRepresentation = transformPayloadRequest.getPayload();

		logger.trace("Received Java object payload.");

		String xmlRepresentation;

		try
		{
			xmlRepresentation = marshaller.marshallJavaObjectToXml(new Marshaller.MarshallJavaObjectToXmlRequest(javaRepresentation,
					characterEncoding, classesToBeBound.getClassesToBeBound())).getXml();

			// TODO: Find better solution. JAXB ignore @XMLTransient on stacktrace for throwables.
			xmlRepresentation = xmlRepresentation.replace("<stackTrace/>", "");

			logger.trace("Created XML payload representation " + xmlRepresentation);
		} catch (Marshaller.JavaObjectOrBoundClassesNotValidException e)
		{
			throw new PayloadMustBeAmenableToTransformationException(
					"Java payload cannot be converted to a textual " + "XML representation.", e);
		}

		// Construct service response.
		return new TransformPayloadResponse(xmlRepresentation);
	}

	private static final Logger logger = LoggerFactory.getLogger(JavaObjectToXmlTextPayloadTransformerBean.class);
	private ServiceValidationUtilities serviceValidationUtilities;
	private Marshaller.MarshallerLocal marshaller;
	private ClassesToBeBound classesToBeBound;
	private String characterEncoding = "UTF-8";

}