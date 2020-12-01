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

import org.urdad.jpa.Entity;

public class EntityFactoryImpl<X, Y extends Entity> implements EntityFactory<X, Y>
{

	public EntityFactoryImpl(Class<X> objectClass, Class<Y> entityClass)
	{
		this.objectClass = objectClass;
		this.entityClass = entityClass;
		objectMapper = new ObjectMapper();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Y produceEntity(X object)
	{
		return (Y) objectMapper.mapObject(object, entityClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public X produceObject(Y entity)
	{
		return (X) objectMapper.mapObject(entity, objectClass);
	}

	private Class<X> objectClass;
	private Class<Y> entityClass;
	private ObjectMapper objectMapper;

}