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

package org.urdad.events.adapter.rpcrest;

import javax.ws.rs.core.Response;
import org.urdad.services.RequestNotValidException;

/** An interface that adapts the services provided by EventPublisher onto RPC rest. */
public interface EventPublisher
{
    /** Determine default options. */
    Response determineDefaultOptions();

    /** Publishes the specified event. */
    Response publishEvent(org.urdad.events.EventPublisher.PublishEventRequest publishEventRequest) throws
        RequestNotValidException, org.urdad.events.EventPublisher.EventMustBeSupportedException;

    interface EventPublisherLocal extends EventPublisher{}

}