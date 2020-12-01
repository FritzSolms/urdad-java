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

package org.urdad.services;

import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/** Thrown to indicate that the 'service provider must be available' pre-condition has been violated. */
@XmlRootElement
@XmlType
@XmlAccessorType(XmlAccessType.PROPERTY)
@JsonTypeName("org.urdad.services.ServiceProviderNotAvailableException")
public class ServiceProviderNotAvailableException extends PreconditionViolation
{

    /**
     * Constructs a new <code>ServiceProviderNotAvailableException</code> exception.
     */
    public ServiceProviderNotAvailableException(){}

	/**
     * Constructs a new <code>ServiceProviderNotAvailableException</code> exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public ServiceProviderNotAvailableException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>ServiceProviderNotAvailableException</code> exception with the specified detail message
     * and cause.
     *
     * @param message the detail message.
     * @param cause the cause.
     */
    public ServiceProviderNotAvailableException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new <code>ServiceProviderNotAvailableException</code> exception with the specified cause.
     *
     * @param cause the cause.
     */
    public ServiceProviderNotAvailableException(Throwable cause)
    {
        super(cause);
    }

}