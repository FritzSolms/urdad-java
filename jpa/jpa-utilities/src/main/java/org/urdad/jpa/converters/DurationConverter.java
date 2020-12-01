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

package org.urdad.jpa.converters;

import com.google.gson.Gson;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Duration;

/** This is used to store a duration in the database.*/
@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, String>
{

	@Override
	public String convertToDatabaseColumn(Duration attribute)
	{
		Gson gson = new Gson();

		return gson.toJson(attribute);
	}

	@Override
	public Duration convertToEntityAttribute(String dbData)
	{
		Gson gson = new Gson();

		return gson.fromJson(dbData,Duration.class);
	}
}