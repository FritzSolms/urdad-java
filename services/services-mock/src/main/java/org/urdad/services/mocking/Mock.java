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

/**
 * Specifies that any Mock object must be able to provide the call logger which is used to log the calls made to it in
 * order to query the call history to assert that pre-conditions and post-conditions of a contract have been addressed.
 *  
 * @author fritz@solms.co.za
 */
@Mocking
public interface Mock
{
	/**
	 * This method returns the call logger (if any) which was assigned to the mock object. It is used by the
	 * {@link CallLoggingAspect} to log call descriptions.
	 * 
	 * @return the call logger used by the mock object.
	 */
	CallLogger getCallLogger();
	
	/**
	 * Method to set the state of a mock object to some specific state in which it behaves in some particular way.
	 * 
	 * @param state the state identifier
	 * @throws InvalidStateException if the mock object does not support the specified state
	 */
	void setState(State state) throws InvalidStateException;
	
	/**
	 * @return the current state of the mock object
	 */
	State getState();
	
	interface State{}
	
	class InvalidStateException extends Exception{}

}