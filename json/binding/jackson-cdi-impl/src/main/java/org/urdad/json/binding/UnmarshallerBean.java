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

package org.urdad.json.binding;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;

public class UnmarshallerBean implements Unmarshaller.UnmarshallerLocal, Unmarshaller.UnmarshallerRemote
{

    @Override
    public UnmarshallJsonToJavaObjectResponse unmarshallJsonToJavaObject(UnmarshallJsonToJavaObjectRequest unmarshallJsonToJavaObjectRequest)
        throws RequestNotValidException, JsonOrBoundClassNotValidException
    {
        // Check pre-condition: Request must be valid.
        serviceValidationUtilities.validateRequest(UnmarshallJsonToJavaObjectRequest.class,
            unmarshallJsonToJavaObjectRequest);

        try
        {
            // Create service response.
            return new UnmarshallJsonToJavaObjectResponse(objectMapper.readValue(unmarshallJsonToJavaObjectRequest.getJson(),
                unmarshallJsonToJavaObjectRequest.getClassToBeBound()));
        }
        catch (IOException e)
        {
            logger.error("Unable to marshall the specified JSON representation to its corresponding Java representation.", e);

            // Check pre-condition: JSON and bound class must be valid.
            throw new JsonOrBoundClassNotValidException(e);
        }
    }

    @PostConstruct
    public void postConstruct()
    {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JaxbAnnotationModule());
        objectMapper.registerSubtypes(classesToBeBound.getClassesToBeBound());
    }

    private static final Logger logger = LoggerFactory.getLogger(MarshallerBean.class);
    @Inject
    private ServiceValidationUtilities serviceValidationUtilities;
    private ObjectMapper objectMapper;
    @Inject
    private ClassesToBeBound classesToBeBound;

}