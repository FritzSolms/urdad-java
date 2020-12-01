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

package ord.urdad.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.urdad.services.RequestNotValidException;
import org.urdad.validation.Validation;

/** Unit tests for services associated with the bean validation domain of responsibility. */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ValidationTestConfiguration.class})
public class ValidationTest
{

    /** Test the 'validateObject' service. Ensure that service succeeds if all pre-conditions are met. */
    @Test
    public void validateObject_preconditionsMet_validObject() throws Exception
    {
        assertTrue(validation.validateObject(new Validation.ValidateObjectRequest(StringContainer.class, new
            StringContainer("content"))).getValid());
    }

    /** Test the 'validateObject' service. Ensure that service succeeds if all pre-conditions are met. */
    @Test
    public void validateObject_preconditionsMet_invalidObject() throws Exception
    {
        assertFalse(validation.validateObject(new Validation.ValidateObjectRequest(StringContainer.class, new
            StringContainer(null))).getValid());
    }

    /**
     * Test the 'validateObject' service. Ensure that service fails if the 'request must be valid' pre-condition is
     * not met. In this scenario a null request is submitted.
     */
    @Test(expected = RequestNotValidException.class)
    public void validateObject_invalidRequest_nullRequest() throws Exception
    {
        validation.validateObject(null);

        fail("A null request must not be accepted.");
    }

    /**
     * Test the 'validateObject' service. Ensure that service fails if the 'request must be valid' pre-condition is
     * not met. In this scenario a request containing a null person is submitted.
     */
    @Test(expected = RequestNotValidException.class)
    public void validateObject_invalidRequest_nullObject() throws Exception
    {
        validation.validateObject(new Validation.ValidateObjectRequest(StringContainer.class, null));

        fail("A null object should not be accepted.");
    }

    /** FIXME: Javadoc */
    private class StringContainer
    {

        public StringContainer(String string)
        {
            this.string = string;
        }

        @NotNull
        private String string;

    }

    // Test subject.
    @Inject
    private Validation.ValidationLocal validation;

}