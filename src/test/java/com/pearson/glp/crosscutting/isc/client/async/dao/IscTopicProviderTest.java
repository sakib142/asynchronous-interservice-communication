package com.pearson.glp.crosscutting.isc.client.async.dao;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.pearson.glp.crosscutting.isc.client.async.cache.TopicCache;
import com.pearson.glp.crosscutting.isc.client.async.config.ZookeeperConfiguration;
import com.pearson.glp.crosscutting.isc.client.async.exception.AsyncException;
import com.pearson.glp.crosscutting.isc.client.async.validation.Validator;

/**
 * The IscTopicProviderTest class.
 * 
 *<p>The class test the topic creation and validation.
 * 
 * @author Md Sakib
 *
 */
public class IscTopicProviderTest {
    /**
     * IscTopicProviderTest instantiate.
     */
    public IscTopicProviderTest() {
        super();
    }

    /**
     * The mock object of ZookeeperConfiguration.
     */
    @Mock
    private ZookeeperConfiguration zookeeperConfiguration;

    /**
     * The mock object of Validator.
     */
    @Mock
    private Validator validator;

    /**
     * The mock object of TopicCache.
     */
    @Mock
    private TopicCache topicCache;

    /**
     * The inject mock object of IscTopicProvider.
     */
    @InjectMocks
    private IscTopicProvider iscTopicProvider = new IscTopicProvider();

    /**
     * The cache map variable.
     */
    private Map<String, String> cacheMap;

    /**
     * Setup and mocking required the object.
     */
    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.cacheMap = new HashMap<>();
        this.cacheMap = mock(HashMap.class);
    }

    /**
     * The method test with whitespace in topic name.
     * 
     * @throws AsyncException
     *             manual exception
     */
    @Test
    private void getTopicFromEventNameWithSpaceTest() throws AsyncException {

        when(this.validator.validate("hello text")).thenReturn("hello_text");
        assertEquals(this.iscTopicProvider.getTopicFromEventName("hello text"),
            "hello_text_Topic");
        verify(this.validator, times(1)).validate("hello text");

    }

    /**
     * The method test whitespace in topic name.
     * 
     * @throws AsyncException
     *             manual exception
     */
    @Test
    private void getTopicFromEventNameWithoutSpace() throws AsyncException {

        when(this.validator.validate("hellotext")).thenReturn("hellotext");
        assertEquals(this.iscTopicProvider.getTopicFromEventName("hellotext"),
            "hellotext_Topic");
        verify(this.validator, times(1)).validate("hellotext");

    }

    /**
     * The method test special character in topic name.
     * 
     * @throws AsyncException
     *             manual exception
     */
    @Test(expectedExceptions = AsyncException.class)
    private void getTopicFromEventNameWithSpecialCharactersTest()
            throws AsyncException {

        when(this.validator.validate("hello text $#%^&*"))
                .thenThrow(AsyncException.class);
        assertEquals(
            this.iscTopicProvider.getTopicFromEventName("hello text $#%^&*"),
            "Event name contains special characters."
                    + " Only ^[a-zA-Z0-9_-]*$ is allowed");
        verify(this.validator, times(1)).validate("hello text $#%^&*");

    }

    /**
     * The method test register topic name.
     * 
     * @throws AsyncException
     *             manual exception
     */
    @Test
    private void registerTopicWithSetTest() throws AsyncException {
        Set<String> eventSet = new HashSet<>();
        eventSet.add("event1");

        this.cacheMap.put("event1", "event1_Topic");
        doNothing().when(this.zookeeperConfiguration)
                .createTopic("event1_Topic");
        when(this.validator.validate("event1")).thenReturn("event1");
        when(this.topicCache.getRegisterMap()).thenReturn(this.cacheMap);
        when(this.cacheMap.putIfAbsent("event1", "event1_Topic"))
                .thenReturn("event1_Topic");
        this.iscTopicProvider.registerTopic(eventSet);
        verify(this.zookeeperConfiguration, times(1))
                .createTopic("event1_Topic");
    }

    /**
     * The method test register topic name.
     * 
     * @throws AsyncException
     *             manual exception
     */
    @Test
    private void registerTopicWithStringTest() throws AsyncException {
        String eventName = "event1";
        this.cacheMap.put(eventName, "event1_Topic");
        doNothing().when(this.zookeeperConfiguration)
                .createTopic("event1_Topic");
        when(this.validator.validate(eventName)).thenReturn(eventName);
        when(this.topicCache.getRegisterMap()).thenReturn(this.cacheMap);
        when(this.cacheMap.putIfAbsent(eventName, "event1_Topic"))
                .thenReturn("event1_Topic");
        this.iscTopicProvider.registerTopic(eventName);

    }

    /**
     * The method test cache for topic name.
     * 
     * @throws AsyncException
     *             manual exception
     */
    @Test
    private void getTopicNameFromCacheWhenNotFoundTest() throws AsyncException {

        String eventName = "event1";
        this.cacheMap.put(eventName, "event1_Topic");
        when(this.validator.validate(eventName)).thenReturn(eventName);
        when(this.topicCache.getRegisterMap()).thenReturn(this.cacheMap);
        when(this.cacheMap.containsKey(eventName)).thenReturn(false);
        assertEquals(this.iscTopicProvider.getTopicNameFromCache(eventName),
            "deadletter");
    }

    /**
     * The method test cache for topic name.
     * 
     * @throws AsyncException
     *             manual exception
     */
    @Test
    private void getTopicNameFromCacheWhenFoundTest() throws AsyncException {

        String eventName = "event1";
        this.cacheMap.put(eventName, "event1_Topic");
        when(this.validator.validate(eventName)).thenReturn(eventName);
        when(this.topicCache.getRegisterMap()).thenReturn(this.cacheMap);
        when(this.cacheMap.containsKey(eventName)).thenReturn(true);
        when(this.cacheMap.get(eventName)).thenReturn("event1_Topic");

        assertEquals(this.iscTopicProvider.getTopicNameFromCache(eventName),
            "event1_Topic");
    }

}
