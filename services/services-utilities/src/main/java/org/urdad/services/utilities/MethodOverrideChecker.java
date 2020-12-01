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
 * A pure function which returns true if the specialized method does override the base method,
 * i.e. if the base method is defined either in one of its super classes or in an interface
 * which is directly or indirectly implemented.
 *
 * @author fritz@solms.co.za
 */
@FunctionalInterface
public interface MethodOverrideChecker 
{
	/**
	 *
	 * @param base the interface or class method which is potentially overridden by the specialized method
	 * @param specialized the method which potentially overrides the base method.
	 * @return true if the specialized method does override the base method and false otherwise.
	 */
	boolean overrides(Method base, Method specialized);

}