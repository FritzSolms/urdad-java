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

package org.urdad.validation;

import java.util.Set;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.urdad.services.RequestNotValidException;

/** The fulfilment of the service contracts that are associated with bean validation. */
@Service
public class ValidationBean implements Validation.ValidationLocal, Validation.ValidationRemote
{

    /** Validates the specified object using the bean validation framework. */
    @Override
    public ValidateObjectResponse validateObject(ValidateObjectRequest validateObjectRequest) throws RequestNotValidException
    {
        // Check pre-condition: Request must be valid.
        if (validateObjectRequest == null)
        {
            throw new RequestNotValidException("A ValidateObjectRequest must be specified.");
        }
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(validateObjectRequest);
        if (!constraintViolations.isEmpty())
        {
            throw new RequestNotValidException(buildMessage(constraintViolations));
        }
        if (validateObjectRequest.getObject() == null)
        {
            throw new RequestNotValidException("A " + validateObjectRequest.getObjectType().getSimpleName() +
                " must be specified.");
        }

        // Use the Bean Validation framework for validation.
        constraintViolations = validator.validate(validateObjectRequest.getObject());

        // Create service response.
        ValidateObjectResponse validateObjectResponse = new ValidateObjectResponse();

        if (!constraintViolations.isEmpty())
        {
            validateObjectResponse.setValid(false);
            validateObjectResponse.setMessage(buildMessage(constraintViolations));
        }
        else
        {
            validateObjectResponse.setValid(true);
        }

        return validateObjectResponse;
    }

    /** Convenience method: construct a message given a set of constraint violations. */
    private String buildMessage(Set<ConstraintViolation<Object>> constraintViolations)
    {
        StringBuilder message = new StringBuilder();

        int count = 0;
        for (ConstraintViolation<?> constraintViolation : constraintViolations)
        {
            count++;

            if (count > 1)
            {
                message.append(" ");
            }

            message.append(constraintViolation.getMessage());
        }

        return message.toString();
    }

    private static final Logger logger = LoggerFactory.getLogger(ValidationBean.class);
    @Inject
    private Validator validator;

}