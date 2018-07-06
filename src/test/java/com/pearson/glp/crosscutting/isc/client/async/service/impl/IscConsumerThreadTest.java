package com.pearson.glp.crosscutting.isc.client.async.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.pearson.glp.crosscutting.isc.client.async.config.IscPropertiesLoader;
import com.pearson.glp.crosscutting.isc.client.async.service.impl.IscConsumerThread;

/**
 * The IscConsumerThreadTest class.
 * 
 * @author Md Sakib
 *
 */
public class IscConsumerThreadTest {
	 /**
     * IscConsumerThreadTest instantiate.
     */
    public IscConsumerThreadTest() {
        super();
    }
    /**
     * The constant variable CONSUMER_POLL_INTERVAL.
     */
    public static final int CONSUMER_POLL_INTERVAL = 1000;
    /**
     * The constant variable MOCKITO_VERIFY_TIMES_VALUE.
     */
    public static final int MOCKITO_VERIFY_TIMES_VALUE = 0;

    /**
     * The mock object of KafkaConsumer.
     */
    @Mock
    private KafkaConsumer<String, String> consumer;
    /**
     * The mock object of Consumer.
     */
    @Mock
    private Consumer<String> consumerFunction;
    /**
     * The mock object of IscPropertiesLoader.
     */
    @Mock
    private IscPropertiesLoader iscPropertiesLoader;
    /**
     * The mock object of IscConsumerThread.
     */
    @InjectMocks
    private IscConsumerThread iscConsumerThread;
 

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
    public void beforeMethod() {
        ConsumerRecord<String, String> consumerRecord;
        List<ConsumerRecord<String, String>> consumerRecordList =
                new ArrayList<>();
        consumerRecord = new ConsumerRecord<>("topic", 1, 1,
                "key", "value");
        TopicPartition topicPartition = new TopicPartition("topic", 1);
        consumerRecordList.add(consumerRecord);

        Map<TopicPartition, List<ConsumerRecord<String, String>>> mapRecords =
                new HashMap<>();
        mapRecords.put(topicPartition, consumerRecordList);
        ConsumerRecords<String, String> consumerRecords =
                new ConsumerRecords<>(mapRecords);

        Mockito.when(
                this.iscPropertiesLoader.getIntegerProperty("poll.interval"))
                .thenReturn(CONSUMER_POLL_INTERVAL);
        Mockito.when(this.consumer.poll(CONSUMER_POLL_INTERVAL))
                .thenReturn(consumerRecords);
        Mockito.doNothing().doThrow(new RuntimeException())
                .when(this.consumerFunction).accept("value");
    }

    /**
     * The method test the thread of consumer.
     */
    @Test
    public void testRun() {
        try {
            this.iscConsumerThread.run();
        } catch (Exception exception) {
            Mockito.verify(this.consumerFunction,
                    Mockito.times(MOCKITO_VERIFY_TIMES_VALUE)).accept("value");
        }

    }

}
