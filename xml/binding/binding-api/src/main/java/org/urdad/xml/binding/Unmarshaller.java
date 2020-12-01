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

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.validation.constraints.NotEmpty;
import org.urdad.services.PreconditionViolation;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;

/** Service contracts that are associated with the conversion of XML to Java objects. */
public interface Unmarshaller
{

    /** FIXME: Javadoc */
    UnmarshallXmlToJavaObjectResponse unmarshallXmlToJavaObject(UnmarshallXmlToJavaObjectRequest
        unmarshallXmlToJavaObjectRequest) throws RequestNotValidException, XmlOrBoundClassesNotValidException;

    /** FIXME: Javadoc. */
    @XmlRootElement
    @XmlType
    @XmlAccessorType(XmlAccessType.PROPERTY)
    class UnmarshallXmlToJavaObjectRequest extends Request
    {

        /** Default constructor. */
        public UnmarshallXmlToJavaObjectRequest(){}

        /** Convenience constructor. */
        public UnmarshallXmlToJavaObjectRequest(String xml, Class... classesToBeBound)
        {
            this.xml = xml;
            this.classesToBeBound = classesToBeBound;
        }

        /** FIXME: Javadoc */
        public String getXml()
        {
            return xml;
        }

        public void setXml(String xml)
        {
            this.xml = xml;
        }

        /** FIXME: Javadoc */
        public Class[] getClassesToBeBound()
        {
            return classesToBeBound;
        }

        public void setClassesToBeBound(Class... classesToBeBound)
        {
            this.classesToBeBound = classesToBeBound;
        }

        @NotEmpty(message = "XML must be specified.")
        private String xml;
        @NotNull(message = "The classes to be bound must be specified.")
        private Class[] classesToBeBound;

    }

    /** FIXME: Javadoc. */
    @XmlRootElement
    @XmlType
    @XmlAccessorType(XmlAccessType.PROPERTY)
    class UnmarshallXmlToJavaObjectResponse extends Response
    {

        /** Default constructor. */
        public UnmarshallXmlToJavaObjectResponse(){}

        /** Convenience constructor. */
        public UnmarshallXmlToJavaObjectResponse(Object javaObject)
        {
            this.javaObject = javaObject;
        }

        /** FIXME: Javadoc */
        public Object getJavaObject()
        {
            return javaObject;
        }

        public void setJavaObject(Object javaObject)
        {
            this.javaObject = javaObject;
        }

        @NotNull(message = "A Java object must be specified.")
        private Object javaObject;

    }

    /**
     * Thrown to indicate that the 'XML and bound class(es) combination must be valid' pre-condition has been
     * violated.
     */
    class XmlOrBoundClassesNotValidException extends PreconditionViolation
    {

        /** Default constructor. */
        public XmlOrBoundClassesNotValidException(){}

        /**
         * Constructs a new <code>XmlOrBoundClassesNotValidException</code> exception with the specified detail message.
         *
         * @param message the detail message.
         */
        public XmlOrBoundClassesNotValidException(String message)
        {
            super(message);
        }

        /**
         * Constructs a new <code>XmlOrBoundClassesNotValidException</code> exception with the specified detail message
         * and cause.
         *
         * @param message the detail message.
         * @param cause the cause.
         */
        public XmlOrBoundClassesNotValidException(String message, Throwable cause)
        {
            super(message, cause);
        }

        /**
         * Constructs a new <code>XmlOrBoundClassesNotValidException</code> exception with the specified cause.
         *
         * @param cause the cause.
         */
        public XmlOrBoundClassesNotValidException(Throwable cause)
        {
            super(cause);
        }

    }

    /** FIXME: Javadoc. */
    interface UnmarshallerLocal extends Unmarshaller{}
    /** FIXME: Javadoc. */
    interface UnmarshallerRemote extends Unmarshaller{}

}