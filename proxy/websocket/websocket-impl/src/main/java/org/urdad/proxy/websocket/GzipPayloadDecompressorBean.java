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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.services.ServiceValidationUtilities;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

/**
 * TODO: Javadoc
 */
public class GzipPayloadDecompressorBean
		implements PayloadDecompressor.PayloadDecompressorLocal, PayloadDecompressor.PayloadDecompressorRemote
{

	public GzipPayloadDecompressorBean(ServiceValidationUtilities serviceValidationUtilities)
	{
		if (serviceValidationUtilities == null)
		{
			throw new RuntimeException("A service validation utilities must be specified.");
		}

		this.serviceValidationUtilities = serviceValidationUtilities;
	}

	public GzipPayloadDecompressorBean(ServiceValidationUtilities serviceValidationUtilities, String characterEncoding)
	{
		if (serviceValidationUtilities == null)
		{
			throw new RuntimeException("A service validation utilities must be specified.");
		}

		this.serviceValidationUtilities = serviceValidationUtilities;
		this.characterEncoding = characterEncoding;
	}

	@Override
	public DecompressPayloadResponse decompressPayload(DecompressPayloadRequest decompressPayloadRequest) throws RequestNotValidException
	{
		logger.trace("Decompressing payload.'");

		// Check pre-condition: Request must be valid.
		serviceValidationUtilities.validateRequest(DecompressPayloadRequest.class, decompressPayloadRequest);

		StringBuilder decompressedPayload = new StringBuilder();

		try (GZIPInputStream gzipInputStream = new GZIPInputStream(
				new ByteArrayInputStream(decompressPayloadRequest.getCompressedPayload()));
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream, characterEncoding)))
		{
			char character;
			while (bufferedReader.ready())
			{
				character = (char) bufferedReader.read();
				decompressedPayload.append(character);
			}

		} catch (IOException e)
		{
			// System error.
			throw new RuntimeException(e);
		}

		// Create service response.
		return new DecompressPayloadResponse(decompressedPayload.toString());
	}

	private static final Logger logger = LoggerFactory.getLogger(GzipPayloadDecompressorBean.class);
	private ServiceValidationUtilities serviceValidationUtilities;

	private String characterEncoding = "UTF-8";
}