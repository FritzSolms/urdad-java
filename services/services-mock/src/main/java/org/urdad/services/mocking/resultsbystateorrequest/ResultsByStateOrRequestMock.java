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

import java.util.HashMap;
import java.util.Map;
import org.urdad.services.Response;
import org.urdad.services.mocking.BaseMock;

/**
 * FIXME: Javadoc
 */
public abstract class ResultsByStateOrRequestMock extends BaseMock
{

    @SuppressWarnings("unchecked")
    protected <T extends Object> T triggerResult(Class<T> responseClass, RequestResultKey requestResultKey) throws Throwable
    {
        Result result = retrieveResultUsingStateThenRequest(requestResultKey);

        if (result instanceof ResponseResult)
        {
        	Response object = ((ResponseResult) result).getObject();

            if (!object.getClass().getName().equals(responseClass.getName()))
            {
                throw new RuntimeException("Object should be of type '" + responseClass + "'.");
            }

            return (T) object;
        }
        else
        {
            throw ((ThrowableResult) result).getThrowable();
        }
    }

    private Result retrieveResultUsingStateThenRequest(RequestResultKey requestResultKey)
    {
    	Result result = resultsByRequestKey.get(new StateAndServiceSignatureResultKey(getState(),requestResultKey.getServiceSignature()));

        if (result != null)
        {
            return result;
        }
	    else
	    {
	        result = resultsByRequestKey.get(new StateResultKey(getState()));
	
	        if (result != null)
	        {
	            return result;
	        }
	        else
	        {
	            result = resultsByRequestKey.get(requestResultKey);
	
	            if(result != null)
	            {
	                return result;
	            }
	            else
	            {
	                 throw new RuntimeException("Unable to locate a result that is associated with the current state or specific request.");
	            }
	        }
        }
    }
	/**
	 * The map of request/result pairs.
	 */
    public Map<ResultKey, Result> getResultsByRequestKey() {
		return resultsByRequestKey;
	}

	protected Map<ResultKey, Result> resultsByRequestKey = new HashMap<>();

}