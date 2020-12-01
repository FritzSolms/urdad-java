/* Copyright 2019 Dr. Fritz Solms & Craig Edwards Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required
 * by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License. */

package org.urdad.proxy.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.json.binding.Unmarshaller;
import org.urdad.json.binding.Unmarshaller.UnmarshallJsonToJavaObjectRequest;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;

/** Transforms JSON-based payloads to their Java object representations. */
public class JsonTextToJavaObjectPayloadTransformerBean
		implements PayloadTransformer.PayloadTransformerLocal, PayloadTransformer.PayloadTransformerRemote
{

	public JsonTextToJavaObjectPayloadTransformerBean(ServiceValidationUtilities serviceValidationUtilities,
			Unmarshaller.UnmarshallerLocal unmarshaller, Class... classesToBeBound)
	{
		super();
		this.serviceValidationUtilities = serviceValidationUtilities;
		this.unmarshaller = unmarshaller;
		this.classesToBeBound = classesToBeBound;
	}

	@Override
	public TransformPayloadResponse transformPayload(TransformPayloadRequest transformPayloadRequest)
			throws RequestNotValidException, PayloadTransformer.PayloadMustBeAmenableToTransformationException
	{
		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(TransformPayloadRequest.class, transformPayloadRequest);

		if (!String.class.isInstance(transformPayloadRequest.getPayload()))
		{
			throw new RequestNotValidException("Only textual (String-based) payloads are supported.");
		}

		String jsonRepresentation = (String) transformPayloadRequest.getPayload();

		logger.trace("Received JSON payload " + jsonRepresentation);

		Object javaRepresentation = null;


		if(transformPayloadRequest.getTargetClass() != null){
			try
			{
				javaRepresentation = unmarshaller
						.unmarshallJsonToJavaObject(new UnmarshallJsonToJavaObjectRequest(jsonRepresentation, transformPayloadRequest.getTargetClass())).getJavaObject();
				logger.trace("Created Java object payload representation.");
			} catch (Unmarshaller.JsonOrBoundClassNotValidException e)
			{
				throw new PayloadMustBeAmenableToTransformationException(e);
			}
		}
		else {
			boolean successfulUnmarshal = false;
			PayloadMustBeAmenableToTransformationException payloadMustBeAmenableToTransformationException = new PayloadMustBeAmenableToTransformationException(
					"JSON payload cannot be converted to a Java " + "object representation.");
			for (Class class1 : classesToBeBound)
		{

			try
			{
				javaRepresentation = unmarshaller
						.unmarshallJsonToJavaObject(new UnmarshallJsonToJavaObjectRequest(jsonRepresentation, class1)).getJavaObject();
				successfulUnmarshal = true;
				logger.trace("Created Java object payload representation.");
				break;
			} catch (Unmarshaller.JsonOrBoundClassNotValidException e)
			{
				payloadMustBeAmenableToTransformationException.addSuppressed(e);
				continue;
			}
		}
		if (!successfulUnmarshal)
		{
			throw payloadMustBeAmenableToTransformationException;
		}}

		// Construct service response.
		return new TransformPayloadResponse(javaRepresentation);
	}

	private static final Logger logger = LoggerFactory.getLogger(JsonTextToJavaObjectPayloadTransformerBean.class);
	private ServiceValidationUtilities serviceValidationUtilities;
	private Unmarshaller.UnmarshallerLocal unmarshaller;
	private Class[] classesToBeBound;
}