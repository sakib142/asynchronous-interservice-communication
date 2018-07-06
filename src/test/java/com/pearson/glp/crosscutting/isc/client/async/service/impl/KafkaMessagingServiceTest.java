package com.pearson.glp.crosscutting.isc.client.async.service.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.pearson.glp.crosscutting.isc.client.async.config.ConsumerConfiguration;
import com.pearson.glp.crosscutting.isc.client.async.config.IscPropertiesLoader;
import com.pearson.glp.crosscutting.isc.client.async.config.ZookeeperConfiguration;
import com.pearson.glp.crosscutting.isc.client.async.dao.IscTopicProvider;
import com.pearson.glp.crosscutting.isc.client.async.exception.AsyncException;
import com.pearson.glp.crosscutting.isc.client.async.service.impl.KafkaMessagingService;
import com.pearson.glp.crosscutting.isc.client.async.service.impl.IscConsumerThread;

/**
 * The KafkaMessagingServiceTest class.
 * 
 * @author Md Sakib
 *
 */
public class KafkaMessagingServiceTest {

	/**
	 * The Logger object.
	 */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(KafkaMessagingService.class);
   
    /**
     * KafkaMessagingServiceTest instantiate.
     */
    public KafkaMessagingServiceTest() {
        super();
    }

    /**
     * The mock object of IscTopicProvider.
     */
    @Mock
    private IscTopicProvider iscTopicProvider;

    /**
     * The mock object of KafkaProducer.
     */
    @Mock
    private KafkaProducer<String, String> producer;

    /**
     * The mock object of Consumer.
     */
    @Mock
    private org.apache.kafka.clients.consumer.Consumer<String, String> consumer;

    /**
     * The mock object of IscPropertiesLoader.
     */
    @Mock
    private IscPropertiesLoader iscPropertiesLoader;

    /**
     * The mock object of ConsumerConfiguration.
     */
    @Mock
    private ConsumerConfiguration consumerConfiguration;

    /**
     * The inject mock object of IscAsyncClient.
     */
    @InjectMocks
    private KafkaMessagingService iscAsyncClient;
    
    /**
     * The consumerFunction variable.
     */
    private Consumer<String> consumerFunction;

    /**
     * The mockConsumer variable.
     */
    private MockConsumer<String, String> mockConsumer;
    
    /**
     * The  mock object of ZookeeperConfiguration.
     */
    @Mock
    private ZookeeperConfiguration zookeeperConfiguration;

    /**
     * Setup and mocking required the object.
     */
    @BeforeClass
    public void beforeClass() {
        MockitoAnnotations.initMocks(this);

    }
    
    /**
     * Setup and mocking required the object.
     */
    @BeforeMethod
    public void beforeMethod() throws AsyncException {
        Set<String> set = new HashSet<>();
        set.add("TEST");
        this.mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
        Mockito.doNothing().when(this.iscTopicProvider).registerTopic(set);

        Mockito.when(this.iscTopicProvider.getTopicFromEventName("TEST"))
                .thenReturn("TEST_TOPIC");
        Mockito.when(this.iscTopicProvider.getTopicFromEventName("TEST_EVENT"))
                .thenReturn("TEST_EVENT_Topic");
        Mockito.when(this.iscTopicProvider.getTopicFromEventName("TEST_EVENT1"))
                .thenReturn("TEST_EVENT1_Topic");

        Mockito.when(this.consumerConfiguration.getKafkaConsumer())
                .thenReturn(this.mockConsumer);
        this.consumerFunction = x -> LOGGER.info(x);

    }

    /**
     * The method test register topic.
     *  
     * @throws AsyncException manual exception
     */
    @Test
    public void testRegister() throws AsyncException {
        HashSet<String> set = new HashSet<>();
        set.add("TEST_EVENT");
        set.add("TEST_EVENT1");

        this.iscAsyncClient.register(set);
        verify(this.iscTopicProvider, times(1)).registerTopic(set);
    }

    /**
     * The method test register topic with string.
     *  
     * @throws AsyncException manual exception
     */
    @Test
    public void testRegisterWithString() throws AsyncException {

        String eventName = "TEST_EVENT";

        this.iscAsyncClient.register(eventName);
        verify(this.iscTopicProvider, times(1)).registerTopic(eventName);
    }

    /**
     * The method test register topic without event name.
     *  
     * @throws AsyncException manual exception
     */
    @Test
    public void testRegisterWithoutEventName() throws AsyncException {
        HashSet<String> set = new HashSet<>();
        this.iscAsyncClient.register(set);
        verify(this.iscTopicProvider, times(1)).registerTopic(set);

    }

    /**
     * The method test subscribe topic without exception.
     *  
     * @throws AsyncException manual exception
     */
    @Test
    public void testSubscribeWithoutException() throws AsyncException {
        IscConsumerThread launcher = Mockito
                .spy(new IscConsumerThread("TEST_EVENT", this.consumerFunction,
                        this.mockConsumer, this.iscPropertiesLoader));

        Thread thread = new Thread(launcher);
        thread.start();
        Map<String, Consumer<String>> eventCallBackMap = new HashMap<>();
        eventCallBackMap.put("TEST_EVENT", this.consumerFunction);
        eventCallBackMap.put("TEST_EVENT1", this.consumerFunction);

        Mockito.when(
                this.zookeeperConfiguration.isTopicExist("TEST_EVENT_Topic"))
                .thenReturn(true);
        Mockito.when(
                this.zookeeperConfiguration.isTopicExist("TEST_EVENT1_Topic"))
                .thenReturn(true);
        this.iscAsyncClient.subscribe(eventCallBackMap);
        thread.stop();
    }

    /**
     * The method test subscribe topic with exception.
     *  
     * @throws AsyncException manual exception
     */
    @Test
    public void testSubscribeWithException() throws AsyncException {
        IscConsumerThread launcher = Mockito
                .spy(new IscConsumerThread("TEST_EVENT", this.consumerFunction,
                        this.mockConsumer, this.iscPropertiesLoader));

        Thread thread = new Thread(launcher);
        thread.start();
        Map<String, Consumer<String>> eventCallBackMap = new HashMap<>();
        eventCallBackMap.put("TEST_EVENT", this.consumerFunction);
        eventCallBackMap.put("TEST_EVENT1", this.consumerFunction);

        Mockito.when(
                this.zookeeperConfiguration.isTopicExist("TEST_EVENT_Topic"))
                .thenReturn(false);
        Mockito.when(
                this.zookeeperConfiguration.isTopicExist("TEST_EVENT1_Topic"))
                .thenReturn(false);
        this.iscAsyncClient.subscribe(eventCallBackMap);
        verify(this.iscTopicProvider, times(1))
                .getTopicFromEventName("TEST_EVENT");
        verify(this.iscTopicProvider, times(1))
                .getTopicFromEventName("TEST_EVENT1");
        thread.stop();
    }

    /**
     * The method test subscribe topic without event name.
     *  
     * @throws AsyncException manual exception
     */
    @Test
    public void testSubscribeWithoutEventnameAndCallbackfunctionMap()
            throws AsyncException {
        IscConsumerThread launcher = Mockito
                .spy(new IscConsumerThread("TEST_EVENT2", this.consumerFunction,
                        this.mockConsumer, this.iscPropertiesLoader));

        Thread thread = new Thread(launcher);
        thread.start();
        Map<String, Consumer<String>> eventCallBackMap = new HashMap<>();
        this.iscAsyncClient.subscribe(eventCallBackMap);
        verify(this.iscTopicProvider, times(0))
                .getTopicFromEventName("TEST_EVENT2");
        thread.stop();
    }

    /**
     * The method test publish message on topic .
     *  
     * @throws AsyncException manual exception
     */
    @Test
    public void testPublishWithConsumerFunctionNotNull() throws AsyncException {
        Mockito.when(this.iscTopicProvider.getTopicNameFromCache("TEST"))
                .thenReturn("TEST_Topic");
        this.iscAsyncClient.publish("TEST", "TEST_JSON", this.consumerFunction);
        verify(this.producer, times(1)).send(Mockito.any(ProducerRecord.class),
                Mockito.any(Callback.class));
    }

    /**
     * The method test publish message on topic with consumer function .
     *  
     * @throws AsyncException manual exception
     */
    @Test
    public void testPublishWithoutConsumerFunction() throws AsyncException {
        Mockito.when(this.iscTopicProvider.getTopicNameFromCache("TEST"))
                .thenReturn("TEST_Topic");
        this.iscAsyncClient.publish("TEST", "TEST_JSON");
        verify(this.producer, times(1)).send(Mockito.any(ProducerRecord.class));
    }

    /**
     * The method test subscribe  topic without exception .
     *  
     * @throws AsyncException manual exception
     */
    @Test
    public void testSubscribeWithoutMapWithoutException()
            throws AsyncException {
        IscConsumerThread launcher = Mockito
                .spy(new IscConsumerThread("TEST_EVENT", this.consumerFunction,
                        this.mockConsumer, this.iscPropertiesLoader));

        Thread thread = new Thread(launcher);
        thread.start();

        Mockito.when(
                this.zookeeperConfiguration.isTopicExist("TEST_EVENT_Topic"))
                .thenReturn(true);

        this.iscAsyncClient.subscribe("TEST_EVENT", this.consumerFunction);
        thread.stop();

    }

    /**
     * The method test subscribe topic with exception .
     *  
     * @throws AsyncException manual exception
     */
    @Test
    public void testSubscribeWithoutMapWithException() throws AsyncException {
        IscConsumerThread launcher = Mockito
                .spy(new IscConsumerThread("TEST_EVENT", this.consumerFunction,
                        this.mockConsumer, this.iscPropertiesLoader));

        Thread thread = new Thread(launcher);
        thread.start();

        Mockito.when(
                this.zookeeperConfiguration.isTopicExist("TEST_EVENT_Topic"))
                .thenReturn(false);

        this.iscAsyncClient.subscribe("TEST_EVENT", this.consumerFunction);
        thread.stop();
    }

}
