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
import org.urdad.xml.binding.Unmarshaller;
import org.urdad.xml.binding.Unmarshaller.UnmarshallerLocal;

/** Transforms XML-based payloads to their Java object representations. */
public class XmlTextToJavaObjectPayloadTransformerBean implements PayloadTransformer.PayloadTransformerLocal,
    PayloadTransformer.PayloadTransformerRemote
{

    public XmlTextToJavaObjectPayloadTransformerBean(ServiceValidationUtilities serviceValidationUtilities, UnmarshallerLocal unmarshaller,
			ClassesToBeBound classesToBeBound)
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

        String xmlRepresentation = (String) transformPayloadRequest.getPayload();

        logger.trace("Received XML payload " + xmlRepresentation);

        Object javaRepresentation;

        try
        {
            javaRepresentation= unmarshaller.unmarshallXmlToJavaObject(new Unmarshaller.UnmarshallXmlToJavaObjectRequest
                (xmlRepresentation, classesToBeBound.getClassesToBeBound())).getJavaObject();

            logger.trace("Created Java object payload representation.");
        }
        catch (Unmarshaller.XmlOrBoundClassesNotValidException e)
        {
            throw new PayloadMustBeAmenableToTransformationException("XML payload cannot be converted to a Java " +
                "object representation.", e);
        }

        // Construct service response.
        return new TransformPayloadResponse(javaRepresentation);
    }

    private static final Logger logger = LoggerFactory.getLogger(XmlTextToJavaObjectPayloadTransformerBean.class);
    private ServiceValidationUtilities serviceValidationUtilities;
    private Unmarshaller.UnmarshallerLocal unmarshaller;
    private ClassesToBeBound classesToBeBound;
}