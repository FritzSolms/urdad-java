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

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.xml.bind.annotation.XmlElement;

/**
 * Introduces the concept of a pre-condition violation which is explicitly represented as a checked exception on
 * each service definition.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class PreconditionViolation extends Exception
{

	public PreconditionViolation(){}

    /**
     * Constructs a new <code>PreconditionViolation</code> exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public PreconditionViolation(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>PreconditionViolation</code> exception with the specified detail message
     * and cause.
     *
     * @param message the detail message.
     * @param cause the cause.
     */
    public PreconditionViolation(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new <code>PreconditionViolation</code> exception with the specified cause.
     *
     * @param cause the cause.
     */
    public PreconditionViolation(Throwable cause)
    {
        super(cause);
    }

    /**
     * Disclaimer: The getter logic and setter method are needed for marshalling purposes.
     * If a cleaner way could be found to do this, it would be good.
     */

    @Override
    @XmlElement
    public String getMessage()
    {
        if (this.message == null || this.message.isEmpty())
        {
            // The message set via constructors should be used in most cases.
            return super.getMessage();
        }
        // This should only happen when using the marshaller, i.e. JAXB marshalling.
        return this.message;
    }

    /** Setter needed for Marshalling purposes. Private so that developers cannot use it as it is not good. */
    private void setMessage(String message)
    {
        this.message = message;
    }

    private String message;


}