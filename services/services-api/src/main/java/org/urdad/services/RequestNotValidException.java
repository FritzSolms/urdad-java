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

/** Thrown to indicate that the 'request must be valid' pre-condition has been violated. */
@XmlRootElement
@XmlType
@XmlAccessorType(XmlAccessType.PROPERTY)
@JsonTypeName("org.urdad.services.RequestNotValidException")
public class RequestNotValidException extends PreconditionViolation
{

    /**
     * Constructs a new <code>RequestNotValidException</code> exception.
     */
    public RequestNotValidException(){}

	/**
     * Constructs a new <code>RequestNotValidException</code> exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public RequestNotValidException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>RequestNotValidException</code> exception with the specified detail message
     * and cause.
     *
     * @param message the detail message.
     * @param cause the cause.
     */
    public RequestNotValidException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new <code>RequestNotValidException</code> exception with the specified cause.
     *
     * @param cause the cause.
     */
    public RequestNotValidException(Throwable cause)
    {
        super(cause);
    }

}