package com.pearson.glp.crosscutting.isc.client.async.config;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEvent;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amazonaws.services.kinesis.model.StreamDescription;
import com.pearson.glp.crosscutting.isc.client.async.service.impl.KinesisMessagingService;

/**
 * The OnLoadStartUpTest test class.
 * 
 * @author Md Sakib
 *
 */
public class OnLoadStartUpTest {

    /**
     * OnLoadStartUpTest instantiate.
     */
    public OnLoadStartUpTest() {
        super();
    }

    @Mock
    private KinesisMessagingService kinesisMessagingService;

    @Mock
    private IscPropertiesLoader iscPropertiesLoader;

    @InjectMocks
    private OnLoadStartUp onLoadStartUp;

    private ApplicationEvent event;

    private static String STREAM_NAME = "abc";

    /**
     * Setup and mocking required the object.
     */
    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Setup before execute method.
     */
    @BeforeMethod
    public void beforeMethod() {
        this.event = Mockito.mock(ApplicationEvent.class);
    }

    @Test
    public void testOnApplicationEvent() {
        StreamDescription description = new StreamDescription();
        description.setStreamStatus("NOTEXISTS");
        Mockito.when(this.kinesisMessagingService.describeStream(STREAM_NAME))
                .thenReturn(description);
        Mockito.doNothing().when(this.kinesisMessagingService)
                .createStream(STREAM_NAME);
        Mockito.when(this.iscPropertiesLoader
                .getStringProperty("spring.application.name"))
                .thenReturn(STREAM_NAME);
        this.onLoadStartUp.onApplicationEvent(this.event);


    }

}
