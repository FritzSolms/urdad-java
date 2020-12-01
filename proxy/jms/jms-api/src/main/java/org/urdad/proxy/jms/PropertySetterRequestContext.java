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

package org.urdad.proxy.jms;

import javax.validation.constraints.NotNull;
import org.urdad.services.Request;

/**
 * TODO: Javadoc
 */
public class PropertySetterRequestContext extends PropertySetterContext
{

    /** Default constructor. */
    public PropertySetterRequestContext(){}

    /** Convience constructor. */
    public PropertySetterRequestContext(Request request)
    {
        this.request = request;
    }

    public Request getRequest()
    {
        return request;
    }

    public void setRequest(Request request)
    {
        this.request = request;
    }

    @NotNull(message = "A request must be specified.")
    private Request request;

}