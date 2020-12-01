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

/** Service contracts that are associated with the conversion of JSON to Java objects. */
public interface Unmarshaller
{

    /** FIXME: Javadoc */
    UnmarshallJsonToJavaObjectResponse unmarshallJsonToJavaObject(UnmarshallJsonToJavaObjectRequest
        unmarshallJsonToJavaObjectRequest) throws RequestNotValidException, JsonOrBoundClassNotValidException;

    /** FIXME: Javadoc. */
    @XmlRootElement
    @XmlType
    @XmlAccessorType(XmlAccessType.PROPERTY)
    class UnmarshallJsonToJavaObjectRequest extends Request
    {

        /** Default constructor. */
        public UnmarshallJsonToJavaObjectRequest(){}

        /** Convenience constructor. */
        public UnmarshallJsonToJavaObjectRequest(String json, Class classToBeBound)
        {
            this.json = json;
            this.classToBeBound = classToBeBound;
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

        /** FIXME: Javadoc */
        public Class getClassToBeBound()
        {
            return classToBeBound;
        }

        public void setClassToBeBound(Class classToBeBound)
        {
            this.classToBeBound = classToBeBound;
        }

        @NotEmpty(message = "JSON must be specified.")
        private String json;
        @NotNull(message = "A class to be bound must be specified.")
        private Class classToBeBound;

    }

    /** FIXME: Javadoc. */
    @XmlRootElement
    @XmlType
    @XmlAccessorType(XmlAccessType.PROPERTY)
    class UnmarshallJsonToJavaObjectResponse extends Response
    {

        /** Default constructor. */
        public UnmarshallJsonToJavaObjectResponse(){}

        /** Convenience constructor. */
        public UnmarshallJsonToJavaObjectResponse(Object javaObject)
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

    /** Thrown to indicate that the 'JSON and bound class combination must be valid' pre-condition has been violated. */
    class JsonOrBoundClassNotValidException extends PreconditionViolation
    {

        /** Default constructor. */
        public JsonOrBoundClassNotValidException(){}

        /**
         * Constructs a new <code>JsonOrBoundClassNotValidException</code> exception with the specified detail message.
         *
         * @param message the detail message.
         */
        public JsonOrBoundClassNotValidException(String message)
        {
            super(message);
        }

        /**
         * Constructs a new <code>JsonOrBoundClassNotValidException</code> exception with the specified detail message
         * and cause.
         *
         * @param message the detail message.
         * @param cause the cause.
         */
        public JsonOrBoundClassNotValidException(String message, Throwable cause)
        {
            super(message, cause);
        }

        /**
         * Constructs a new <code>JsonOrBoundClassNotValidException</code> exception with the specified cause.
         *
         * @param cause the cause.
         */
        public JsonOrBoundClassNotValidException(Throwable cause)
        {
            super(cause);
        }

    }

    interface UnmarshallerLocal extends Unmarshaller{}
    interface UnmarshallerRemote extends Unmarshaller{}

}