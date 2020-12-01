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

import java.io.StringWriter;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;

/**
 * FIXME: Javadoc
 */
@Stateless
@Local(Marshaller.MarshallerLocal.class)
@Remote(Marshaller.MarshallerRemote.class)
public class MarshallerBean implements Marshaller.MarshallerLocal, Marshaller.MarshallerRemote
{

    /** FIXME: Javadoc */
    @Override
    public MarshallJavaObjectToXmlResponse marshallJavaObjectToXml(MarshallJavaObjectToXmlRequest
        marshallJavaObjectToXmlRequest) throws RequestNotValidException, JavaObjectOrBoundClassesNotValidException
    {
        // Check pre-condition: Request must be valid.
        serviceValidationUtilities.validateRequest(MarshallJavaObjectToXmlRequest.class, marshallJavaObjectToXmlRequest);

        StringWriter stringWriter = new StringWriter();

        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(marshallJavaObjectToXmlRequest.getClassesToBeBound());
            javax.xml.bind.Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, marshallJavaObjectToXmlRequest.getCharacterEncoding());
            if ((marshallJavaObjectToXmlRequest.getFormatXml() != null) && (marshallJavaObjectToXmlRequest
                .getFormatXml()))
            {
                marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            }
            marshaller.marshal(marshallJavaObjectToXmlRequest.getJavaObject(), stringWriter);
        }
        catch (JAXBException e)
        {
            logger.error("Unable to marshall the specified Java representation to its corresponding XML representation.", e);

            // Check pre-condition: Java object and bound class(es) must be valid.
            throw new JavaObjectOrBoundClassesNotValidException(e);
        }

        // Create service response.
        return new MarshallJavaObjectToXmlResponse(stringWriter.toString());
    }

    private static final Logger logger = LoggerFactory.getLogger(MarshallerBean.class);
    @Inject
    private ServiceValidationUtilities serviceValidationUtilities;

}