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

package org.urdad.services.mocking.resultsbystateorrequest;

import java.util.Arrays;

import javax.validation.constraints.NotNull;

import org.urdad.services.mocking.Mock;
import org.urdad.services.mocking.Mock.State;

/**
 * FIXME: Javadoc
 */
public class StateAndServiceSignatureResultKey implements ResultKey
{

    public StateAndServiceSignatureResultKey(State state, String serviceSignature) {
		super();
		this.state = state;
		this.serviceSignature = serviceSignature;
	}

	/** The state that forms part of the key. */
    public Mock.State getState()
    {
        return state;
    }
    /**
     * The request type that forms part of the key.
     */
    @Override
    public String getServiceSignature() {
    	return serviceSignature;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || getClass() != object.getClass())
        {
            return false;
        }

        StateAndServiceSignatureResultKey objectState = (StateAndServiceSignatureResultKey) object;

        Boolean sameState= state != null ? state.equals(objectState.state) : objectState.state == null;
        Boolean sameType= serviceSignature != null ? serviceSignature.equals(objectState.serviceSignature) : objectState.serviceSignature == null;
        
        return sameState && sameType;
    }

    @Override
    public int hashCode()
    {
    	return Arrays.deepHashCode(new Object[]{state,serviceSignature});
    }



	@NotNull(message = "A state must be specified.")
    private Mock.State state;
    
    @NotNull(message="A service signature must be specified.")
    private String serviceSignature;

}