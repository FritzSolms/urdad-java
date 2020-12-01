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
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.urdad.services.Request;

/**
 * Logging aspect for Mock objects which intercepts all mock object calls and 
 * adds the call events to the call history of the mock object.
 * 
 * @author fritz@solms.co.za
 */
@Aspect
public class CallLoggingAspect 
{

	/**
	 * Logs all service calls, but not other method calls.
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around(value = "@within(org.urdad.services.mocking.Mocking)" + "|| @annotation(org.urdad.services.mocking" +
		".Mocking)")
	public Object logCall(ProceedingJoinPoint joinPoint) throws Throwable 
	{
		LocalDateTime requestTime = LocalDateTime.now();
		 
		Method method = ((MethodSignature)joinPoint.getStaticPart().getSignature()).getMethod();
		Request request = null;
		try
		{
			request = (Request)joinPoint.getArgs()[0];
		}
		catch (Exception e){ /* Ignore all non-service requests, i.e. methods which do not receive a single service request */ }
		
		Object response;
		LocalDateTime responseTime;
		
		try
		{
		  response = joinPoint.proceed();
		  responseTime = LocalDateTime.now();
			 
		  if (request != null)
			  ((Mock)joinPoint.getTarget()).getCallLogger().logCall(
					new CallDescriptor(method, requestTime, request, response, responseTime));
		  return response;
		}
		catch (Exception e)
		{
			responseTime = LocalDateTime.now();
			response = e;
			((Mock)joinPoint.getTarget()).getCallLogger().logCall(
					new CallDescriptor(method, requestTime, request, response, responseTime));
			throw e;
		}
    }

}