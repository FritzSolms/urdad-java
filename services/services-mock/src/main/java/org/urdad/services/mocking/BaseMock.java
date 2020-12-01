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
 * A minimal base class for mocking objects which ensures that the call logging aspect is applied to the logging object
 * and which can provide the call logger which was assigned to the mock it.
 * 
 * @author fritz@solms.co.za
 */
@Mocking
public class BaseMock implements Mock 
{

	public BaseMock() 
	{
		callLogger = new SimpleCallLogger(this);
	}

	@Override
	public CallLogger getCallLogger() 
	{
		return callLogger;
	}

	public State getState()
	{
		return state;
	}

	public void setState(State state)
	{
		this.state = state;
	}

	private CallLogger callLogger;
	private State state;

}