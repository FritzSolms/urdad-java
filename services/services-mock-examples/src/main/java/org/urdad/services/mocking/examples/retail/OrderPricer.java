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

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.urdad.services.Response;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.mocking.examples.shipping.ShippingQuoteProvider.DoNotShipToAddressException;
import org.urdad.services.mocking.examples.shipping.ShippingQuoteProvider.RailwayOnStrikeException;

public interface OrderPricer 
{

	GetOrderCostResponse getOrderCost(GetOrderCostRequest getOrderCostRequest) throws RequestNotValidException,
		DoNotShipToAddressException, ItemPricer.ItemNotAvailableException, RailwayOnStrikeException;
	
	class GetOrderCostRequest extends Request
	{
		
		public GetOrderCostRequest(Order order) {
			super();
			this.order = order;
		}

		public Order getOrder() {
			return order;
		}

		public void setOrder(Order order) {
			this.order = order;
		}

		@Valid
		@NotNull
		private Order order;
	}
	
	class GetOrderCostResponse extends Response
	{
		
		public GetOrderCostResponse(double cost) {
			super();
			this.cost = cost;
		}

		public double getCost() {
			return cost;
		}

		public void setCost(double cost) {
			this.cost = cost;
		}

		@Min(0)
		private double cost;
	}
	
}