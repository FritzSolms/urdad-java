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

package org.urdad.jpa.factory;

import java.util.Map;
import java.util.TreeMap;
import javax.enterprise.inject.Default;

/** TODO: Javadoc. */
@SuppressWarnings("rawtypes")
@Default
public class EntityFactoryContainer
{

	public EntityFactory retrieveEntityFactory(Class objectClass)
	{
		return entityFactoriesByObjectClassName.get(objectClass.getName());
	}

	public void registerEntityFactory(Class objectClass, EntityFactory entityFactory)
	{
		entityFactoriesByObjectClassName.put(objectClass.getName(), entityFactory);
	}

	private Map<String, EntityFactory> entityFactoriesByObjectClassName = new TreeMap<>();

}