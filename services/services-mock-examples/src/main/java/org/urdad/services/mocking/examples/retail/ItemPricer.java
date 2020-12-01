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

import org.urdad.services.PreconditionViolation;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;
import org.urdad.services.mocking.examples.Person;

public interface ItemPricer
{
	
	GetItemPriceResponse getItemPrice(GetItemPriceRequest getItemPriceRequest) throws RequestNotValidException,
		ItemNotAvailableException;

	class GetItemPriceRequest extends Request
	{

		public GetItemPriceRequest(Person person, String itemCode) {
			super();
			this.person = person;
			this.itemCode = itemCode;
		}

		public Person getPerson() {
			return person;
		}

		public void setPerson(Person person) {
			this.person = person;
		}

		public String getItemCode() {
			return itemCode;
		}

		public void setItemCode(String itemCode) {
			this.itemCode = itemCode;
		}

		private Person person;
		private String itemCode;
	}

	class GetItemPriceResponse extends Response
	{
		public GetItemPriceResponse(double price)
		{
			this.price = price;
		}
		public double getPrice() {
			return price;
		}

		public void setPrice(double price) {
			this.price = price;
		}

		private double price;
	}

	class ItemNotAvailableException extends PreconditionViolation
	{
		public ItemNotAvailableException()
		{
			super();
		}

		public ItemNotAvailableException(String message, Throwable cause) {
			super(message, cause);
		}

		public ItemNotAvailableException(String message) {
			super(message);
		}

		public ItemNotAvailableException(Throwable cause) {
			super(cause);
		}
	}

}