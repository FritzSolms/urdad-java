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

package org.urdad.services.mocking;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import org.urdad.services.Request;

/**
 * A call log which contains the method being requested, the request object and time, 
 * the response (response object or exception) as well as the response time.
 * 
 * @author fritz@solms.co.za
 *
 */
public class CallDescriptor
{	
	public CallDescriptor(
			Method method,
			LocalDateTime requestTime,
			Request request,
			Object response, // Either a response object or an exception.
			LocalDateTime responseTime)
	{
		super();
		this.requestTime = requestTime;
		this.method = method;
		this.request = request;
		this.response = response;
		this.responseTime = responseTime;
	}
	
	public LocalDateTime getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(LocalDateTime requestTime) {
		this.requestTime = requestTime;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}
	
	public LocalDateTime getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(LocalDateTime responseTime) {
		this.responseTime = responseTime;
	}
	
	public String toString()
	{
		return method.getName() + " called at " + requestTime + " with " + request + " returned at "
		  + responseTime + " with " + response;
	}

	private LocalDateTime requestTime, responseTime;
	private Method method;
	private Request request;
	private Object response; // Either a response object or an exception.

}