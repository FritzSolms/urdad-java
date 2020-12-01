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

package org.urdad.services.utilities;

import java.lang.reflect.Method;
import org.urdad.services.Request;
import org.urdad.services.Response;

/**
 * Simple implementation of the {@link ServiceUtilities} contract.
 * 
 * @author fritz@solms.co.za
 * @see ServiceUtilities
 */
public class ServiceUtilitiesBean implements ServiceUtilities
{

	@Override
	public Class<? extends java.lang.Object> getRequestClass(java.lang.Object serviceProvider, String serviceName) throws
		NotAServiceException
	{
		return getRequestClass(getService(serviceProvider, serviceName));
	}

	@Override
	public Class<? extends java.lang.Object> getResponseClass(java.lang.Object serviceProvider, String serviceName) throws
		NotAServiceException
	{
		return getResponseClass(getService(serviceProvider, serviceName));
	}

	@Override
	public Class<? extends java.lang.Object> getRequestClass(Method service) throws NotAServiceException
	{
		if (service.getParameters().length != 1)
			throw new NotAServiceException("More than one parameter");
		else
		{
			Class<? extends java.lang.Object> requestClass = service.getParameters()[0].getType();
			if (Request.class.isAssignableFrom(requestClass))
				return requestClass;
			else
				throw new NotAServiceException("Parameter not a Request class");
		}
	}

	@Override
	public Class<? extends java.lang.Object> getResponseClass(Method service) throws NotAServiceException
	{
		Class<? extends java.lang.Object> responseClass = service.getReturnType();
		if (Response.class.isAssignableFrom(responseClass))
			return responseClass;
		else
			throw new NotAServiceException("Return type not a Object class");
	}

	@Override
	public Method getService(java.lang.Object serviceProvider, String serviceName) throws NotAServiceException
	{
		for (Method service: serviceProvider.getClass().getMethods())
		{
			if (service.getName().equals(serviceName))
			{
				getRequestClass(service); // Will check whether a service
				return service;
			}
		}	
		throw new NotAServiceException("Method with that name does not exist");
	}

}