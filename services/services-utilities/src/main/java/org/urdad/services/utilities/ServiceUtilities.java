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
import org.urdad.services.PreconditionViolation;

/**
 * A container for some utility functions around services.
 * 
 * @author fritz@solms.co.za
 */
public interface ServiceUtilities 
{

	/**
	 * Returns the request class for a specified service of a specified service provider
	 * @param serviceProvider the service provider class hosting the required service
	 * @param serviceName the name of the service whose request class is required
	 * @return the request class for the specified service
	 * @throws NotAServiceException if either there is no method with serviceName or 
	 *  that method does not comply to the requirements of a service, i.e. does not 
	 *  have a single parameter of type Request and a return value of type Object.
	 */
	Class<? extends Object> getRequestClass(Object serviceProvider, String serviceName) throws NotAServiceException;

	/**
	 * Returns the response class for a specified service of a specified service provider
	 * @param serviceProvider the service provider class hosting the required service
	 * @param serviceName the name of the service whose request class is required
	 * @return the request class for the specified service
	 * @throws NotAServiceException if either there is no method with serviceName or 
	 *  that method does not comply to the requirements of a service, i.e. does not 
	 *  have a single parameter of type Request and a return value of type Object.
	 */
	Class<? extends Object> getResponseClass(Object serviceProvider, String serviceName) throws NotAServiceException;
	
	/**
	 * Returns the request class for a specified service.
	 * @param service the name of the service whose request class is required
	 * @return the request class for the specified service
	 * @throws NotAServiceException if the method does not comply to the 
	 *  requirements of a service, i.e. does not have a single parameter of type 
	 *  Request and a return value of type Object.
	 */
	Class<? extends Object> getRequestClass(Method service) throws NotAServiceException;
	
	/**
	 * Returns the response class for a specified service.
	 * @param service the name of the service whose request class is required
	 * @return the request class for the specified service
	 * @throws NotAServiceException if the method does not comply to the 
	 *  requirements of a service, i.e. does not have a single parameter of type 
	 *  Request and a return value of type Object.
	 */
	Class<? extends Object> getResponseClass(Method service) throws NotAServiceException;
	
	/**
	 * Returns the service (Method) of a specified service provider based on the service name.
	 * @param serviceProvider the service provider class hosting the required service
	 * @param methodName the name of the service whose request class is required
	 * @return the Method for the requested service.
	 * @throws NotAServiceException if the method does not comply to the requirements of a service, i.e. does not
	 * have a single parameter of type Request and a return value of type Object.
	 */
	Method getService(Object serviceProvider, String methodName) throws NotAServiceException;
	
	/**
	 * An exception which is thrown if a service either does not exist or does not conform 
	 * to the requirements of a service (single request parameter and a response return type).
	 * 
	 * @author fritz@solms.co.za
	 */
	class NotAServiceException extends PreconditionViolation
	{
		public NotAServiceException() { super(); }
		public NotAServiceException(String message) { super(message); }
		public NotAServiceException(Throwable cause) { super(cause); }
		public NotAServiceException(String message, Throwable cause) {super(message, cause);}
	}
		
}