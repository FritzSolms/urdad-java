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

/**
 * A simple implementation of the {@link MethodOverrideChecker} contract.
 * 
 * @author fritz@solms.co.za
 * @see MethodOverrideChecker
 * 
 */
public class MethodOverrideCheckerBean implements MethodOverrideChecker 
{

	@Override
	public boolean overrides(Method base, Method specialized) 
	{	
		// FIXME - over-simplified
		if (!base.getName().equals(specialized.getName()))
			return false;
		else
			return sameParameters(base, specialized); // && sameReturnType(base, specialized);
	}
	
	/**
	 * 
	 * @param m1 method 1
	 * @param m2 method 2
	 * @return true if methods m1 and m2 have the same method parameter signatures
	 */
	private boolean sameParameters(Method m1, Method m2)
	{
		if (m1.getParameterCount() != m2.getParameterCount())
			return false;

		for (int i=0; i<m1.getParameters().length; ++i)
			if (!m1.getParameters()[i].getType().equals(m2.getParameters()[i].getType()))
				return false;
		
		return true;
	}
	
	/**
	 * 
	 * @param m1 method 1
	 * @param m2 method 2
	 * @return true if methods m1 and m2 have the same return types
	 */
	private Boolean sameReturnType(Method m1, Method m2)
	{
		if (m1.getReturnType().equals(m2.getReturnType()))
			return true;
		else
			return false;
	}

}