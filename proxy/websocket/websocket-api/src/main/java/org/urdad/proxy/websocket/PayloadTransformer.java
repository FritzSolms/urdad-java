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

import org.urdad.services.PreconditionViolation;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * TODO: Javadoc
 */
public interface PayloadTransformer
{

    /** Transforms the specified payload into an alternative format. */
    TransformPayloadResponse transformPayload(TransformPayloadRequest transformPayloadRequest) throws
        RequestNotValidException, PayloadMustBeAmenableToTransformationException;

    /** TODO: Javadoc */
    class TransformPayloadRequest extends Request
    {

       /** Default constructor. */
        public TransformPayloadRequest(){}

        /** Convenience constructor. */
        public TransformPayloadRequest(Object payload)
        {
            this.payload = payload;
        }

        public TransformPayloadRequest(Object payload, Class targetClass) {
            this.payload = payload;
            this.targetClass = targetClass;
        }

        /** TODO: Javadoc */
        public Object getPayload()
        {
            return payload;
        }

        public void setPayload(Object payload)
        {
            this.payload = payload;
        }

        public Class getTargetClass() {
            return targetClass;
        }

        public void setTargetClass(Class targetClass) {
            this.targetClass = targetClass;
        }

        @NotNull(message= "A payload must be specified.")
        @Valid
        private Object payload;

        private Class targetClass;

    }

    /** TODO: Javadoc */
    class TransformPayloadResponse extends Response
    {
        /** Default constructor. */
        public TransformPayloadResponse(){}

        /** Convenience constructor. */
        public TransformPayloadResponse(Object payload)
        {
            this.payload = payload;
        }

        /** TODO: Javadoc */
        public Object getPayload()
        {
            return payload;
        }

        public void setPayload(Object payload)
        {
            this.payload = payload;
        }

        @NotNull(message= "A payload must be specified.")
        @Valid
        private Object payload;

    }

    /** Thrown to indicate that the 'payload must be amenable to transformation' pre-condition has been violated. */
    class PayloadMustBeAmenableToTransformationException extends PreconditionViolation
    {

        /**
         * Constructs a new <code>PayloadTypeNotSupportedException</code> exception with the specified detail
         * message.
         *
         * @param message the detail message.
         */
        public PayloadMustBeAmenableToTransformationException(String message)
        {
            super(message);
        }

        /**
         * Constructs a new <code>PayloadMustBeAmenableToTransformationException</code> exception with the specified detail
         * message and cause.
         *
         * @param message the detail message.
         * @param cause the cause.
         */
        public PayloadMustBeAmenableToTransformationException(String message, Throwable cause)
        {
            super(message, cause);
        }

        /**
         * Constructs a new <code>PayloadMustBeAmenableToTransformationException</code> exception with the specified cause.
         *
         * @param cause the cause.
         */
        public PayloadMustBeAmenableToTransformationException(Throwable cause)
        {
            super(cause);
        }

    }

    /** TODO: Javadoc. */
    interface PayloadTransformerLocal extends PayloadTransformer{}

    /** TODO: Javadoc. */
    interface PayloadTransformerRemote extends PayloadTransformer{}

}
