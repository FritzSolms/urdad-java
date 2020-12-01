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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;

@Service
public class MarshallerBean implements Marshaller.MarshallerLocal, Marshaller.MarshallerRemote
{

    @Override
    public MarshallJavaObjectToJsonResponse marshallJavaObjectToJson(MarshallJavaObjectToJsonRequest marshallJavaObjectToJsonRequest)
        throws RequestNotValidException, JavaObjectNotValidException
    {
        // Check pre-condition: Request must be valid.
        serviceValidationUtilities.validateRequest(MarshallJavaObjectToJsonRequest.class, marshallJavaObjectToJsonRequest);

        try
        {
            // Create service response.
            return new MarshallJavaObjectToJsonResponse(objectMapper.writeValueAsString(marshallJavaObjectToJsonRequest.getJavaObject()));
        }
        catch (JsonProcessingException e)
        {
            logger.error("Unable to marshall the specified Java representation to its corresponding JSON representation.", e);

            throw new JavaObjectNotValidException(e);
        }
    }

    @PostConstruct
    public void postConstruct()
    {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JaxbAnnotationModule());
    }

    private static final Logger logger = LoggerFactory.getLogger(MarshallerBean.class);
    @Inject
    private ServiceValidationUtilities serviceValidationUtilities;
    private ObjectMapper objectMapper;

}