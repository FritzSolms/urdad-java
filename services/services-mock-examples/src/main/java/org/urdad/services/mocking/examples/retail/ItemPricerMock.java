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

package org.urdad.services.mocking.examples.retail;

import java.util.Set;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.springframework.stereotype.Service;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.mocking.BaseMock;

@Stateless
@Service
public class ItemPricerMock extends BaseMock implements ItemPricer
{
	@Override
	public GetItemPriceResponse getItemPrice(GetItemPriceRequest getItemPriceRequest) throws RequestNotValidException,
		ItemNotAvailableException
	{
        // Check pre-condition: Request must be valid.
		if (getItemPriceRequest == null)
		{
			throw new RequestNotValidException("A request must be specified.");
		}
		// Use the Bean Validation framework for validation.
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(getItemPriceRequest);
		if (!constraintViolations.isEmpty())
		{
			throw new RequestNotValidException(buildMessage(constraintViolations));
		}

		if (getItemPriceRequest.getItemCode().equals("notAvailable"))
		{
			throw new ItemNotAvailableException();
		}

		return new GetItemPriceResponse(99.99);
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

	@Inject
	private Validator validator;

}