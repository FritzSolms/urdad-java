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

package org.urdad.services.utilities;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.urdad.services.Response;
import org.urdad.services.Request;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceUtilitiesTestConfiguration.class})
/**
 * A unit test for ServiceUtilities implementations. 
 * 
 * @author fritz@solms.co.za
 */
public class ServiceUtilitiesTest 
{

	@Test
	public void testRequestClassForValidService() 
	{
		DummyServiceProvider dummyServiceProvider = new DummyServiceProviderBean();
		try
		{
			Class<? extends java.lang.Object> requestClass
				= serviceUtilities.getRequestClass(dummyServiceProvider, "service1");
			assertTrue(requestClass.getName().equals
				("org.urdad.services.utilities.ServiceUtilitiesTest$DummyServiceProvider$Service1Request"));
		}
		catch (Exception e)
		{
			fail("Could not retrieve valid service.");
		}
	}
	
	@Test
	public void testRequestClassForWhenNoMethodWithRequestedServiceName()
	{
		DummyServiceProvider dummyServiceProvider = new DummyServiceProviderBean();
		try
		{
			serviceUtilities.getRequestClass(dummyServiceProvider, "unknownService");
			fail("Found service which does not exist");
		}
		catch (Exception e) {}
	}

	@Test
	public void testRequestClassForWhenMethodNotComplyingToServiceRequirements()
	{
		DummyServiceProvider dummyServiceProvider = new DummyServiceProviderBean();
		try
		{
			serviceUtilities.getRequestClass(dummyServiceProvider, "service2");
			fail("Found service2 though does not comply to service requirements");

			serviceUtilities.getRequestClass(dummyServiceProvider, "service3");
			fail("Found service3 though does not comply to service requirements");
		}
		catch (Exception e) {}
	}

	@Test
	public void testResponseClassForValidService() 
	{
		DummyServiceProvider dummyServiceProvider = new DummyServiceProviderBean();
		try
		{
			Class<? extends java.lang.Object> responseClass
				= serviceUtilities.getResponseClass(dummyServiceProvider, "service1");
			assertTrue(responseClass.getName().equals
				("org.urdad.services.utilities.ServiceUtilitiesTest$DummyServiceProvider$Service1Response"));
		}
		catch (Exception e)
		{
			fail("Could not retrieve valid service.");
		}
	}

	@Inject
	private ServiceUtilities serviceUtilities;
	
	public interface DummyServiceProvider
	{
		Service1Response service1(Service1Request request);
		Service2Response service2(Service2Request request, int extraParam);
		int service3(int nonReqeustParam);
		
		class Service1Request extends Request{}
		class Service1Response extends Response
        {}
		class Service2Request extends Request{}
		class Service2Response extends Response
        {}
	}

	public class DummyServiceProviderBean implements DummyServiceProvider
	{
		@Override
		public Service1Response service1(Service1Request request) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Service2Response service2(Service2Request request, int extraParam) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int service3(int nonReqeustParam) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}

}