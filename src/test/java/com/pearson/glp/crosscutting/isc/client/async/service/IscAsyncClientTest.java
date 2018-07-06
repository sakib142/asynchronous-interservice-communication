package com.pearson.glp.crosscutting.isc.client.async.service;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.pearson.glp.crosscutting.isc.client.async.config.IscPropertiesLoader;
import com.pearson.glp.crosscutting.isc.client.async.service.IscAsyncClient;
import com.pearson.glp.crosscutting.isc.client.async.service.MessagingService;

/**
 * The IscAsyncClientTest class.
 * 
 * @author Md Sakib
 *
 */
public class IscAsyncClientTest {

    /**
     * IscAsyncClientTest instantiate.
     */
    public IscAsyncClientTest() {
        super();
    }

    private final static String EVENT_NAME = "abc";

    /**
     * The mock object of IscPropertiesLoader.
     */
    @Mock
    private IscPropertiesLoader iPLoader;

    /**
     * The mock object of MessagingService.
     */
    @Mock
    private MessagingService iscClient;

    /**
     * Inject mock object of KinesisMessagingService.
     */
    @InjectMocks
    private IscAsyncClient iscAsyncClient;


    private Consumer<String> callBackFunction = x -> {
        System.out.println("Consume message : "
                + x);
    };

    /**
     * Setup and mocking required the object.
     */
    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(this.iPLoader.getStringProperty("stream.name")).thenReturn("abc");
        when(this.iPLoader.getStringProperty("default.event.name"))
                .thenReturn("abc");
        when(this.iPLoader.getStringProperty("shard.type"))
                .thenReturn("LATEST");

    }

    @BeforeMethod
    public void beforeMethod() {
       
    }

    /**
     * The method used to test register event name.
     */
    @Test
    public void testRegister() {
        this.iscAsyncClient.register(EVENT_NAME);
    }

    /**
     * The method used to test set register event name.
     */
    @Test
    public void testRegisterSet() {
        Set<String> eventSet = new HashSet<>();
        eventSet.add(EVENT_NAME);
        this.iscAsyncClient.register(eventSet);

    }

    /**
     * The method used to test publish data on event name.
     */
    @Test
    public void testPublish() {
        String data = "Hello test message";

        this.iscAsyncClient.publish(EVENT_NAME, data);

    }

    /**
     * The method used to test publish data on event name with callback.
     */
    @Test
    public void testPublishCallback() {
        String data = "Hello test message";

        this.iscAsyncClient.publish(EVENT_NAME, data, callBackFunction);

    }

    /**
     * The method used to test subscribe data on event name with callback.
     */
    @Test
    public void testSubscribe() {

        this.iscAsyncClient.subscribe(EVENT_NAME, callBackFunction);
        
    }
    
    /**
     * The method used to test publish data on event name with callback.
     */
    @Test
    public void testSubscribeWithMap() {

        Map<String, Consumer<String>> eventCallBackMap  = new HashMap<>();
        eventCallBackMap.put(EVENT_NAME, callBackFunction);
        this.iscAsyncClient.subscribe(eventCallBackMap);
        
    }

}
