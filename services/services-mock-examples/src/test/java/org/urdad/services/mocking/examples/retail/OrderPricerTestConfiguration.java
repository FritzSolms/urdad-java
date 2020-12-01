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

package org.urdad.services.mocking.examples.retail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.urdad.services.mocking.CallLoggingAspect;
import org.urdad.services.mocking.examples.shipping.ShippingQuoteProvider;
import org.urdad.services.mocking.examples.shipping.ShippingQuoteProviderMock;
import org.urdad.services.utilities.ServiceUtilities;
import org.urdad.services.utilities.ServiceUtilitiesBean;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class OrderPricerTestConfiguration 
{

    @Bean
    public CallLoggingAspect callLoggingAspect()
    {
        logger.trace("Constructing call logging aspect.");
        return new CallLoggingAspect();
    }

    @Bean
    public OrderPricer orderPricer()
    {
        logger.trace("Constructing order pricer.");
        return new OrderPricerBean();
    }

    @Bean
    public ItemPricer itemPricerMock()
    {
        logger.trace("Constructing mock item pricer.");
        return new ItemPricerMock();
    }

    @Bean
    public ShippingQuoteProvider shippingQuoteProviderMock()
    {
        logger.trace("Constructing mock shipping quote provider.");
        return new ShippingQuoteProviderMock();
    }

    @Bean
    public javax.validation.Validator validator()
    {
        logger.trace("Constructing validator.");
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public ServiceUtilities serviceUtilities()
    {
        logger.trace("Constructing service utilities.");
        return new ServiceUtilitiesBean();
    }

    private static final Logger logger = (Logger)LoggerFactory.getLogger(OrderPricerTestConfiguration.class);

}