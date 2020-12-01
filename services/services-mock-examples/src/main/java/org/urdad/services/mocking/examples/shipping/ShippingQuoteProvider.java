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

import javax.validation.constraints.NotNull;
import org.urdad.services.Response;
import org.urdad.services.PreconditionViolation;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.mocking.examples.Address;

public interface ShippingQuoteProvider 
{

	GetQuoteResponse getQuote(GetQuoteRequest getQuoteRequest) throws RequestNotValidException,
		DoNotShipToAddressException, RailwayOnStrikeException;
	
	class GetQuoteRequest extends Request
	{
		
		public GetQuoteRequest(Address address) {
			super();
			this.address = address;
		}

		public Address getAddress() {
			return address;
		}

		public void setAddress(Address address) {
			this.address = address;
		}

		@NotNull
		private Address address;
	}
	
	class GetQuoteResponse extends Response
	{
		
		public GetQuoteResponse(double price)
		{
			super();
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

	class RailwayOnStrikeException extends PreconditionViolation
	{

		public RailwayOnStrikeException(){}

		public RailwayOnStrikeException(String msg) {super(msg);}

		public RailwayOnStrikeException(Throwable cause) {super(cause);}

		public RailwayOnStrikeException(String msg, Throwable cause) {super(msg,cause);}

	}

	class DoNotShipToAddressException extends PreconditionViolation
	{

		public DoNotShipToAddressException() {super();}

		public DoNotShipToAddressException(String message, Throwable cause) 
		{
			super(message, cause);
		}

		public DoNotShipToAddressException(String message) {super(message);}

		public DoNotShipToAddressException(Throwable cause) {super(cause);}

	}

}