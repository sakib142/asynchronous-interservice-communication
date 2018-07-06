package com.pearson.glp.crosscutting.isc.client.async.validation;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.pearson.glp.crosscutting.isc.client.async.exception.AsyncException;

/**
 * The ValidatorTest class.
 * 
 *<p>The class validate the event name.
 * 
 * @author Md Sakib
 *
 */
public class ValidatorTest {
	 /**
     * ValidatorTest instantiate.
     */
    public ValidatorTest() {
        super();
    }
    /**
     * The instance variable eventName.
     */
    private String eventName = "hello test";

    /**
     * The inject mock object of Validator.
     */
    @InjectMocks
    private Validator validator;

    /**
     * Setup and mocking required the object.
     */
    @BeforeClass
    public void beforeClass() {
        MockitoAnnotations.initMocks(this);

    }

    /**
     * The method test event name not null.
     * 
     * @throws AsyncException manual exception
     */
    @Test(expectedExceptions = AsyncException.class)
    public void testValidateEventNameNotNull() throws AsyncException {
        this.eventName = null;
        String result = this.validator.validate(this.eventName);

        Assert.assertNotNull(result, "Event name cannot be null or blank");

    }

    /**
     * The method test event name having no space.
     * 
     *<p>The validate method remove the space from string.
     * 
     * @throws AsyncException manual exception
     */
    @Test
    public void testValidateEventNameHavingSpace() throws AsyncException {
        this.eventName = "hello test";
        String result = this.validator.validate(this.eventName);

        Assert.assertEquals(result, "hello_test");

    }

    /**
     * The method test event name having no special character.
     * 
     *<p>The validate method remove the special character from string.
     * 
     * @throws AsyncException manual exception
     */
    @Test(expectedExceptions = AsyncException.class)
    public void testValidateEventNameHavingSpecialCharacterWithException()
            throws AsyncException {
        this.eventName = "            hello test  @#$^&**^%     ";

        String result = this.validator.validate(this.eventName);

        Assert.assertEquals(result,
                "Event name contains special characters. Only ^[a-zA-Z0-9_-]*$"
                        + " is allowed");

    }

}
