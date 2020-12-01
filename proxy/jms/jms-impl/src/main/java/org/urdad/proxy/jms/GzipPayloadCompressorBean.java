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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import javax.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;

/**
 * TODO: Javadoc
 */
public class GzipPayloadCompressorBean implements PayloadCompressor.PayloadCompressorLocal, PayloadCompressor.PayloadCompressorRemote
{

	public GzipPayloadCompressorBean(ServiceValidationUtilities serviceValidationUtilities)
	{
		if (serviceValidationUtilities == null)
		{
			throw new RuntimeException("A service validation utilities must be specified.");
		}

		this.serviceValidationUtilities = serviceValidationUtilities;
	}

	public GzipPayloadCompressorBean(ServiceValidationUtilities serviceValidationUtilities, String characterEncoding)
	{
		if (serviceValidationUtilities == null)
		{
			throw new RuntimeException("A service validation utilities must be specified.");
		}

		this.serviceValidationUtilities = serviceValidationUtilities;
		this.characterEncoding = characterEncoding;
	}

	@Override
	public CompressPayloadResponse compressPayload(CompressPayloadRequest compressPayloadRequest) throws RequestNotValidException
	{
		logger.trace("Compressing payload.'");

		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(CompressPayloadRequest.class, compressPayloadRequest);

		byte[] compressedPayload;

		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream))
		{
			if (compressPayloadRequest.getPayload() instanceof String)
			{
				gzipOutputStream.write(((String) compressPayloadRequest.getPayload()).getBytes(characterEncoding));
				gzipOutputStream.close();
				compressedPayload = byteArrayOutputStream.toByteArray();
			}
			else
			{
				throw new RuntimeException("Only string-based payloads are currently supported.");
			}
		} catch (IOException e)
		{
			// System error.
			throw new RuntimeException(e);
		}

		return new CompressPayloadResponse(compressedPayload);
	}

	private static final Logger logger = LoggerFactory.getLogger(GzipPayloadCompressorBean.class);
	private ServiceValidationUtilities serviceValidationUtilities;

	@NotEmpty(message = "A character encoding must be specified.")
	private String characterEncoding = "UTF-8";

}