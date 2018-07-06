package com.pearson.glp.crosscutting.isc.client.async.model;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * The StreamInformationTest class.
 * 
 * @author Md Sakib
 *
 */
public class StreamInformationTest {

    /**
     * StreamInformationTest instantiate.
     */
    public StreamInformationTest() {
        super();
    }

    /**
     * Setup and mocking required the object.
     */
    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testObject() {
        StreamInformation streamInfo = new StreamInformation();
        assertNotNull(streamInfo);

    }

    @Test
    public void testGetSet() {
        StreamInformation streamInfo = new StreamInformation();
        streamInfo.setId(1);
        streamInfo.setEventName("abc");
        streamInfo.setStreamName("test");
        assertEquals(streamInfo.getStreamName(), "test");
        assertEquals(streamInfo.getId(), 1);
        assertEquals(streamInfo.getEventName(), "abc");

    }
}
