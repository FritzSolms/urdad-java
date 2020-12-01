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

package org.urdad.services.mocking.resultsbystateorrequest;

import java.io.StringWriter;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.urdad.services.Request;

/**
 * FIXME: Javadoc
 */
public class JaxbXmlRepresentationRequestResultKey extends RequestResultKey
{

    public JaxbXmlRepresentationRequestResultKey(Request request, Class... classesToBeBound)
    {
    	super(request.getClass().getName());
        this.xmlRepresentation = generateXmlRepresentation(request, classesToBeBound);
    }

    public JaxbXmlRepresentationRequestResultKey(Request request, JAXBContext jaxbContext)
    {
    	super(request.getClass().getName());
        this.xmlRepresentation = generateXmlRepresentation(request, jaxbContext);
    }

    public String getXmlRepresentation()
    {
        return xmlRepresentation;
    }

    private String generateXmlRepresentation(Request request, JAXBContext jaxbContext)
    {
        StringWriter stringWriter = new StringWriter();

        try
        {
            javax.xml.bind.Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(request, stringWriter);
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }

        return stringWriter.toString();
    }

    private String generateXmlRepresentation(Request request, Class... classesToBeBound)
    {
        try
        {
            return generateXmlRepresentation(request,  JAXBContext.newInstance(classesToBeBound));
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || getClass() != object.getClass())
        {
            return false;
        }

        JaxbXmlRepresentationRequestResultKey objectXmlRepresentation = (JaxbXmlRepresentationRequestResultKey) object;

        Boolean sameXml= xmlRepresentation != null ? xmlRepresentation.equals(objectXmlRepresentation.xmlRepresentation) : objectXmlRepresentation
            .xmlRepresentation == null;
        Boolean sameSignature= getServiceSignature() != null ? getServiceSignature().equals(objectXmlRepresentation.getServiceSignature()) : objectXmlRepresentation
                .getServiceSignature() == null;
        return sameSignature && sameXml;
    }

    @Override
    public int hashCode()
    {
        return Arrays.deepHashCode(new Object[]{getServiceSignature(),xmlRepresentation});
    }

    private String xmlRepresentation;

}