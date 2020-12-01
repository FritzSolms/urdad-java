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

import javax.validation.constraints.NotNull;
import org.urdad.services.mocking.Mock;

/**
 * FIXME: Javadoc
 */
@Deprecated
public class StateResultKey implements ResultKey
{

    public StateResultKey(Mock.State state)
    {
        this.state = state;
    }

    /** The state that contains the value for the key. */
    public Mock.State getState()
    {
        return state;
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

        StateResultKey objectState = (StateResultKey) object;

        return state != null ? state.equals(objectState.state) : objectState.state == null;
    }

    @Override
    public int hashCode()
    {
        return state != null ? state.hashCode() : 0;
    }

    @NotNull(message = "A state must be specified.")
    private Mock.State state;

	@Override
	public String getServiceSignature() {
		return null;
	}

}