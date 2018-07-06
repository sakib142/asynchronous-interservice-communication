package com.pearson.glp.crosscutting.isc.client.async.model;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * The EventMessageTest class.
 * 
 * @author Md Sakib
 *
 */
public class EventMessageTest {

    /**
     * EventMessageTest instantiate.
     */
    public EventMessageTest() {
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
    public void testObject(){
        EventMessage eventMessage = new EventMessage();
        assertNotNull(eventMessage);
        
        
    }
    
    @Test
    public void testGetSet(){
        EventMessage eventMessage = new EventMessage();
        eventMessage.setEventName("test");
        eventMessage.setId("123");
        eventMessage.setPayload("test");
        eventMessage.setTimestamp("132423");
        
        assertEquals(eventMessage.getEventName(), "test");
        assertEquals(eventMessage.getId(), "123");
        assertEquals(eventMessage.getPayload(), "test");
        assertEquals(eventMessage.getTimestamp(), "132423");
        
        
        
    }
}
