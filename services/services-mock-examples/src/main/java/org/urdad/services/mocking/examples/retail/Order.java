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

import org.urdad.services.mocking.examples.Person;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class Order
{

	public Order(Person buyer, Map<String, Integer> orderItems) {
		super();
		this.buyer = buyer;
		this.orderItems = orderItems;
	}
	
	public Person getBuyer() {
		return buyer;
	}

	public void setBuyer(Person buyer) {
		this.buyer = buyer;
	}

	public Map<String, Integer> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(Map<String, Integer> orderItems) {
		this.orderItems = orderItems;
	}
	
	@NotNull
	private Person buyer;
	@NotNull
	@NotEmpty
	private Map<String,Integer> orderItems = new HashMap<>();

}