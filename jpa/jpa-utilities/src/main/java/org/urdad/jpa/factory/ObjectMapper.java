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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import javax.persistence.Entity;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ObjectMapper
{
	public Object mapObject(Object inputObject, Class<?> outputClazz)
	{
		//Is this mappable, otherwise skip
		if(outputClazz.isInterface())
		{
			return null;
		}
		if(Modifier.isAbstract(outputClazz.getModifiers()))
		{
			return null;
		}
		if (inputObject == null)
		{
			return null;
		}

		Object returnObject;
		try
		{
			returnObject = outputClazz.newInstance();
		} catch(InstantiationException | IllegalAccessException e1)
		{
			throw new RuntimeException(e1);
		}
		for (Method sourceGetter : inputObject.getClass().getMethods())
		{
			if (sourceGetter.getName().startsWith("get") && !sourceGetter.getName().equals("getClass"))
			{
				try
				{
					Method targetSetter = null;
					try
					{
						targetSetter = outputClazz.getMethod(sourceGetter.getName().replace("get", "set"), sourceGetter.getReturnType());
					} catch(NoSuchMethodException e)
					{
						String fullClassName = outputClazz.getName();
						String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1, fullClassName.length());
						Method[] methods = outputClazz.getMethods();

						if (("get" + className + "Identifier").equalsIgnoreCase(sourceGetter.getName()))
						{
							if (!(returnObject instanceof org.urdad.jpa.Entity))
							{

								for (Method method : methods)
								{
									if (method.getName().equals("setIdentifier"))
									{
										targetSetter = method;
										break;
									}
								}
							}

							if (targetSetter == null)
							{
								continue;
							}
						} else if (("getIdentifier").equalsIgnoreCase(sourceGetter.getName()))
						{
							if (returnObject instanceof org.urdad.jpa.Entity)
							{
								for (Method method : methods)
								{
									if (method.getName().equals("set" + className + "Identifier"))
									{
										targetSetter = method;
										break;
									}
								}
							}
							if (targetSetter == null)
							{
								continue;
							}
						} else
                        {
                            try
                            {
                            	String sourceTypeName = sourceGetter.getReturnType().getName();
                            	String targetTypePackage;
                            	if(sourceTypeName.contains("jpa")) {
                            		targetTypePackage = sourceTypeName.replaceAll(".jpa."+sourceGetter.getReturnType().getSimpleName(), "");
                            	} else {
                            		targetTypePackage = sourceTypeName.replaceAll(sourceGetter.getReturnType().getSimpleName(), "jpa");
                            		
                            	}
                            	String targetType = targetTypePackage+= "."+sourceGetter.getReturnType().getSimpleName();
                                targetSetter = outputClazz.getMethod(sourceGetter.getName().replace("get", "set"), Class.forName(targetType ));
                            } catch (NoSuchMethodException e1)
                            {
                                continue;
                            } catch (ClassNotFoundException e1)
							{
                            	continue;
							}
                        }
					}

					if ((sourceGetter.getReturnType().isAnnotationPresent(Entity.class) || targetSetter.getParameterTypes()[0].isAnnotationPresent(Entity.class)))
					{
						targetSetter.invoke(returnObject, mapObject(sourceGetter.invoke(inputObject), targetSetter.getParameterTypes()[0]));

					} else if (Collection.class.isAssignableFrom(sourceGetter.getReturnType()))
					{

						// TODO add support for collections of entities when reflection can circumvent type erasure
						Collection sourceCollection = (Collection) sourceGetter.invoke(inputObject);
						Collection targetCollection;
						try
						{
							targetCollection = (Collection) outputClazz.getMethod(sourceGetter.getName()).invoke(returnObject);
						} catch(NoSuchMethodException e)
						{
							throw new RuntimeException(e);
						}
						try
						{
							targetCollection.addAll(sourceCollection);
						} catch(ClassCastException e)
						{
							throw new RuntimeException("This mapper cannot handle objects with collections of entities.");
						}
					} else
					{
						targetSetter.invoke(returnObject, sourceGetter.invoke(inputObject));

					}
				} catch(SecurityException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		return returnObject;
	}
}
