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

import java.time.LocalDateTime;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.urdad.services.Response;
import org.urdad.services.Request;

/**
 * Logging interceptor for Mock objects which intercepts all mock object calls and adds the call events to the call
 * history of the mock object.
 * 
 * @author fritz@solms.co.za
 *
 */
@Interceptor
@Mocking
public class CallLoggingInterceptor 
{
	/**
	 * The actual interceptor method which intercepts all calls to all
	 * services of mocking service providers, i.e. service provider classes
	 * which have been annotated as {@link Mocking}.
	 * 
	 * @param invocationContext the context of the invocation including information of what is intercepted and how the
	 * intercepted object was called.
	 * @return the return value of the intercepted method (or it will raise the exception raised by the intercepted
	 * method)
	 * @throws Exception the exception raised by the intercepted method
	 */
	@AroundInvoke
	public java.lang.Object logCall(InvocationContext invocationContext) throws Exception
	{	
		System.out.println("Logging call");
		LocalDateTime requestTime = LocalDateTime.now();
		Request request = (Request)invocationContext.getParameters()[0];

		java.lang.Object object;
		LocalDateTime responseTime;
		
		try
		{
		  	object = (Response) invocationContext.proceed();

		  	responseTime = LocalDateTime.now();

		  	((Mock)invocationContext.getTarget()).getCallLogger().logCall(new CallDescriptor(invocationContext
			  .getMethod(), requestTime, request, object, responseTime));

			return object;
		}
		catch (Exception e)
		{
			responseTime = LocalDateTime.now();

			object = e;

			((Mock)invocationContext.getTarget()).getCallLogger().logCall(new CallDescriptor(invocationContext
				.getMethod(), requestTime, request, object, responseTime));

			throw e;
		}
	}

}