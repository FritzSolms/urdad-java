/* Copyright 2019 Dr. Fritz Solms & Craig Edwards Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required
 * by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License. */

package org.urdad.proxy.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.json.binding.Marshaller;
import org.urdad.json.binding.Marshaller.JavaObjectNotValidException;
import org.urdad.json.binding.Marshaller.MarshallJavaObjectToJsonRequest;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;

/** Transforms Java-based object payloads to their textual JSON representations. */
public class JavaObjectToJsonTextPayloadTransformerBean
		implements PayloadTransformer.PayloadTransformerLocal, PayloadTransformer.PayloadTransformerRemote
{

	public JavaObjectToJsonTextPayloadTransformerBean(ServiceValidationUtilities serviceValidationUtilities,
			Marshaller.MarshallerLocal marshaller)
	{
		super();
		this.serviceValidationUtilities = serviceValidationUtilities;
		this.marshaller = marshaller;
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
			xmlRepresentation = marshaller.marshallJavaObjectToJson(new MarshallJavaObjectToJsonRequest(javaRepresentation)).getJson();

			// TODO: Find better solution. JAXB ignore @XMLTransient on stacktrace for throwables.
			xmlRepresentation = xmlRepresentation.replace("<stackTrace/>", "");

			logger.trace("Created XML payload representation " + xmlRepresentation);
		} catch (JavaObjectNotValidException e)
		{
			throw new PayloadMustBeAmenableToTransformationException(
					"Java payload cannot be converted to a textual " + "JSON representation.", e);
		}

		// Construct service response.
		return new TransformPayloadResponse(xmlRepresentation);
	}

	private static final Logger logger = LoggerFactory.getLogger(JavaObjectToJsonTextPayloadTransformerBean.class);
	private ServiceValidationUtilities serviceValidationUtilities;
	private Marshaller.MarshallerLocal marshaller;

}