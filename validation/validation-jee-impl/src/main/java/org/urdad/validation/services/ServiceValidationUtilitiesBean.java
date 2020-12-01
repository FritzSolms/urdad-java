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

package org.urdad.validation.services;

import javax.ejb.Stateless;
import javax.inject.Inject;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;
import org.urdad.validation.Validation;

/**
 * FIXME: Javadoc
 */
@Stateless
public class ServiceValidationUtilitiesBean implements ServiceValidationUtilities
{

    /** FIXME: Javadoc */

    public <T extends Request> void validateRequest(Class<T> requestType, T request) throws RequestNotValidException
    {
        Validation.ValidateObjectResponse validateObjectResponse = validation.validateObject(new Validation
            .ValidateObjectRequest<>(requestType, request));

        if (!validateObjectResponse.getValid())
        {
            throw new RequestNotValidException(validateObjectResponse.getMessage());
        }
    }

    /** FIXME: Javadoc */
    public <T extends Response> void validateResponse(Class<T> responseType, T response) throws RequestNotValidException
    {
        Validation.ValidateObjectResponse validateObjectResponse = validation.validateObject(new Validation
            .ValidateObjectRequest<>(responseType, response));

        if (!validateObjectResponse.getValid())
        {
            throw new RequestNotValidException(validateObjectResponse.getMessage());
        }
    }

    @Inject
    private Validation.ValidationLocal validation;

}