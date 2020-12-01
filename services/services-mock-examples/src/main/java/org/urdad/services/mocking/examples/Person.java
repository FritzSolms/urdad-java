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

package org.urdad.services.mocking.examples;

import java.time.Duration;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;

public class Person 
{

	public Person(String name, LocalDate dateOfBirth, Address address)
	{
		this.name = name;
		this.dateOfBirth = dateOfBirth;
		this.address = address;
	}

	public String getName()
	{
		return name;
	}

	public int getAge()
	{
		return (int) Duration.between(dateOfBirth, LocalDate.now()).toDays() / 365;
	}

	public Address getAddress()
	{
		return address;
	}

	public void setAddress(Address address)
	{
		this.address = address;
	}

	@NotNull
	@NotEmpty
	private String name;
	@NotNull
	private LocalDate dateOfBirth;
	@NotNull
	private Address address;

}