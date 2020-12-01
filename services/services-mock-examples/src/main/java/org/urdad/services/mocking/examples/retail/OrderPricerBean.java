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

import java.util.Map;
import java.util.Set;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.springframework.stereotype.Service;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.mocking.examples.Address;
import org.urdad.services.mocking.examples.shipping.ShippingQuoteProvider;

@Stateless
@Service
public class OrderPricerBean implements OrderPricer
{

	@Override
	public GetOrderCostResponse getOrderCost(GetOrderCostRequest getOrderCostRequest) throws RequestNotValidException,
        ShippingQuoteProvider.DoNotShipToAddressException, ItemPricer.ItemNotAvailableException, ShippingQuoteProvider
        .RailwayOnStrikeException
	{
        // Check pre-condition: Request must be valid.
        if (getOrderCostRequest == null)
        {
            throw new RequestNotValidException("A request must be specified.");
        }
        // Use the Bean Validation framework for validation.
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(getOrderCostRequest);
        if (!constraintViolations.isEmpty())
        {
            throw new RequestNotValidException(buildMessage(constraintViolations));
        }

        Map<String,Integer> orderItems = getOrderCostRequest.getOrder().getOrderItems();
        double total = 0;

        for (String itemCode : orderItems.keySet())
        {
        	ItemPricer.GetItemPriceRequest req = new ItemPricer.GetItemPriceRequest(getOrderCostRequest.getOrder().getBuyer(),
                itemCode);
        	total += itemPricer.getItemPrice(req).getPrice() * orderItems.get(itemCode);
        }
        
        Address shippingAddress = getOrderCostRequest.getOrder().getBuyer().getAddress();
    	total += shippingQuoteProvider.getQuote(new ShippingQuoteProvider.GetQuoteRequest(shippingAddress)).getPrice();
    	
		return new GetOrderCostResponse(total);
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
    @Inject
    private ItemPricer itemPricer;
    @Inject
    private ShippingQuoteProvider shippingQuoteProvider;

}