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

package org.urdad.proxy.jms;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotEmpty;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;

/**
 * TODO: Javadoc
 */
public interface PayloadCompressor
{

    /** Compresses the specified payload. */
    CompressPayloadResponse compressPayload(CompressPayloadRequest compressPayloadRequest) throws RequestNotValidException;

    class CompressPayloadRequest extends Request
    {

        /** Default constructor. */
        public CompressPayloadRequest(){}

        /** Convenience constructor. */
        public CompressPayloadRequest(Object payload)
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

    class CompressPayloadResponse extends Response
    {

        /** Default constructor. */
        public CompressPayloadResponse(){}

        /** Convenience constructor. */
        public CompressPayloadResponse(byte[] compressedPayload)
        {
            this.compressedPayload = compressedPayload;
        }

        /** TODO: Javadoc */
        public byte[] getCompressedPayload()
        {
            return compressedPayload;
        }

        public void setCompressedPayload(byte[] compressedPayload)
        {
            this.compressedPayload = compressedPayload;
        }

        @NotEmpty(message= "A compressed payload must be specified.")
        private byte[] compressedPayload;

    }

    interface PayloadCompressorLocal extends PayloadCompressor{}
    interface PayloadCompressorRemote extends PayloadCompressor{}

}
