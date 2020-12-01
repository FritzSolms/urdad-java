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
import java.util.List;
import org.urdad.services.utilities.ServiceUtilities.NotAServiceException;

/**
 * A contract for a utility class which can maintain the call log for a mock object.
 * 
 * @author fritz@solms.co.za
 *
 */
public interface CallLogger 
{
	
	/**
	 * Logs a specific call made to a mock object.
	 * @param callDescriptor the call to be logged.
	 */
	void logCall(CallDescriptor callDescriptor);
	
	/**
	 * 
	 * @return the list of call logs describing the calls made to the mock object.
	 */
	List<CallDescriptor> getCallLog();

	/**
	 * The number of invocations made to the specified method (i.e. any concrete 
	 * method which implements the method signature or is that method or overrides this method).
	 * @param method the method who's invocation count is required
	 * @return the number of invocations
	 */
	int getInvocationCount(Method method);

	/**
	 * The number of invocations made to the service identified by the provided service name
	 * (as per our services oriented approach), i.e. any concrete 
	 * method which implements the method signature or is that method or overrides this method).
	 * @param serviceName the method who's invocation count is required
	 * @return the number of invocations
	 * @throws NotAServiceException when there is either no method with that name which complies 
	 * to the requirements of a service
	 */
	int getInvocationCount(String serviceName) throws NotAServiceException;

	/**
	 * Clears the call history removing all stored call descriptors.
	 */
	void clearLog();
	
	/**
	 * 
	 * @return the mock object whose requests the call logger logs.
	 */
	BaseMock getMockObject();

}