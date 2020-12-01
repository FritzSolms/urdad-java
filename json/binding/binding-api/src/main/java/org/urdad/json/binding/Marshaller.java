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

/** Service contracts that are associated with the conversion of Java objects to JSON. */
public interface Marshaller
{

    /** FIXME: Javadoc */
    MarshallJavaObjectToJsonResponse marshallJavaObjectToJson(MarshallJavaObjectToJsonRequest marshallJavaObjectToJsonRequest) throws
        RequestNotValidException, JavaObjectNotValidException;

    @XmlRootElement
    @XmlType
    @XmlAccessorType(XmlAccessType.PROPERTY)
    class MarshallJavaObjectToJsonRequest extends Request
    {

        /** Default constructor. */
        public MarshallJavaObjectToJsonRequest(){}

        /** Convenience constructor. */
        public MarshallJavaObjectToJsonRequest(Object javaObject)
        {
            this.javaObject = javaObject;
        }

        /** Convenience constructor. */
        public MarshallJavaObjectToJsonRequest(Object javaObject, Boolean formatJson)
        {
            this.javaObject = javaObject;
            this.formatJson = formatJson;
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

        /** FIXME: Javadoc */
        public Boolean getFormatJson()
        {
            return formatJson;
        }

        public void setFormatJson(Boolean formatJson)
        {
            this.formatJson = formatJson;
        }

        @NotNull(message = "A Java object must be specified.")
        private Object javaObject;
        private Boolean formatJson = false;

    }

    @XmlRootElement
    @XmlType
    @XmlAccessorType(XmlAccessType.PROPERTY)
    class MarshallJavaObjectToJsonResponse extends Response
    {

        /** Default constructor. */
        public MarshallJavaObjectToJsonResponse(){}

        /** Convenience constructor. */
        public MarshallJavaObjectToJsonResponse(String json)
        {
            this.json = json;
        }

        /** FIXME: Javadoc */
        public String getJson()
        {
            return json;
        }

        public void setJson(String json)
        {
            this.json = json;
        }

        @NotEmpty(message = "JSON must be specified.")
        private String json;

    }

    /** Thrown to indicate that the 'Java object and bound class(es) combination must be valid' pre-condition has been violated. */
    class JavaObjectNotValidException extends PreconditionViolation
    {

        /** Default constructor. */
        public JavaObjectNotValidException(){}

        /**
         * Constructs a new <code>JavaObjectOrBoundClassesNotValidException</code> exception with the specified detail message.
         *
         * @param message the detail message.
         */
        public JavaObjectNotValidException(String message)
        {
            super(message);
        }

        /**
         * Constructs a new <code>JavaObjectOrBoundClassesNotValidException</code> exception with the specified detail
         * message and cause.
         *
         * @param message the detail message.
         * @param cause the cause.
         */
        public JavaObjectNotValidException(String message, Throwable cause)
        {
            super(message, cause);
        }

        /**
         * Constructs a new <code>JavaObjectOrBoundClassesNotValidException</code> exception with the specified cause.
         *
         * @param cause the cause.
         */
        public JavaObjectNotValidException(Throwable cause)
        {
            super(cause);
        }

    }

    interface MarshallerLocal extends Marshaller{}
    interface MarshallerRemote extends Marshaller{}

}