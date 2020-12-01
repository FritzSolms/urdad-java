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

package org.urdad.services.mocking.examples.shipping;

import java.util.Set;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.springframework.stereotype.Service;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.mocking.BaseMock;
import org.urdad.services.mocking.Mock;

@Stateless
@Service
public class ShippingQuoteProviderMock extends BaseMock implements ShippingQuoteProvider
{
	public ShippingQuoteProviderMock()
	{
		setState(State.externalRequirementsMet);
	}
	
	public GetQuoteResponse getQuote(GetQuoteRequest getQuoteRequest)
		throws RequestNotValidException, DoNotShipToAddressException, RailwayOnStrikeException
	{
		// Check pre-condition: Request must be valid.
		if (getQuoteRequest == null)
		{
			throw new RequestNotValidException("A request must be specified.");
		}
		// Use the Bean Validation framework for validation.
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(getQuoteRequest);
		if (!constraintViolations.isEmpty())
		{
			throw new RequestNotValidException(buildMessage(constraintViolations));
		}

		if (getState() == State.railwayOnStrike)
		{
			throw new RailwayOnStrikeException();
		}

		if (getQuoteRequest.getAddress().getCity().trim().toLowerCase().equals("timbuktu"))
		{
			throw new DoNotShipToAddressException();
		}

		if (getQuoteRequest.getAddress().getCity().trim().toLowerCase().equals("pofadder"))
		{
			return new GetQuoteResponse(0);
		}
		else
		{
			return new GetQuoteResponse(888.88);
		}
	}
	
	public enum State implements Mock.State{externalRequirementsMet, railwayOnStrike}

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