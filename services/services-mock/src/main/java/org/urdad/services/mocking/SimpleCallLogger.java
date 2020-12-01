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
import java.util.LinkedList;
import java.util.List;
import org.urdad.services.utilities.MethodOverrideChecker;
import org.urdad.services.utilities.MethodOverrideCheckerBean;
import org.urdad.services.utilities.ServiceUtilities;
import org.urdad.services.utilities.ServiceUtilities.NotAServiceException;
import org.urdad.services.utilities.ServiceUtilitiesBean;

/**
 * A basic implementation of a call logger which maintains a list of call descriptors.
 * 
 * @author fritz@solms.co.za
 */
public class SimpleCallLogger implements CallLogger 
{

	public SimpleCallLogger(BaseMock mock)
	{
		this.mock = mock;
	}
	
	@Override
	public void logCall(CallDescriptor callDescriptor) 
	{
		callLog.add(callDescriptor); 
	}

	@Override
	public List<CallDescriptor> getCallLog() { return callLog; }

	@Override
	public int getInvocationCount(Method method) 
	{
		int numCalls = 0;
		for (CallDescriptor callDescriptor : callLog)
		{
			if (methodOverrideChecker.overrides(method, callDescriptor.getMethod()))
				numCalls++;
		}
		return numCalls;
	}

	@Override
	public BaseMock getMockObject() { return mock; }
	
	@Override
	public int getInvocationCount(String serviceName) throws NotAServiceException
	{
		Method method = serviceUtilities.getService(mock, serviceName);
		return getInvocationCount(method);
	}

	@Override
	public void clearLog() { callLog.clear(); }

	private BaseMock mock;
	private ServiceUtilities serviceUtilities = new ServiceUtilitiesBean();
	private MethodOverrideChecker methodOverrideChecker = new MethodOverrideCheckerBean();
	private List<CallDescriptor> callLog = new LinkedList<>();

}