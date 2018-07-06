package com.pearson.glp.crosscutting.isc.client.async.service;

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

public class IscConsumerThreadTest {

	@Mock
	private KafkaConsumer<String, String> consumer;

	@Mock
	Consumer<String> consumerFunction;

	@Mock
	IscPropertiesLoader iscPropertiesLoader;

	@InjectMocks
	private IscConsumerThread iscConsumerThread;

	ConsumerRecord<String, String> consumerRecord;

	@BeforeClass
	public void beforeClass() {
		MockitoAnnotations.initMocks(this);

	}

	@BeforeMethod
	public void beforeMethod() {
		List<ConsumerRecord<String, String>> consumerRecordList = new ArrayList<ConsumerRecord<String, String>>();
		consumerRecord = new ConsumerRecord<String, String>("topic", 1, 1, "key", "value");
		TopicPartition topicPartition = new TopicPartition("topic", 2);
		consumerRecordList.add(consumerRecord);
		Map<TopicPartition, List<ConsumerRecord<String, String>>> mapRecords = new HashMap<TopicPartition, List<ConsumerRecord<String, String>>>();
		mapRecords.put(topicPartition, consumerRecordList);
		ConsumerRecords<String, String> consumerRecords = new ConsumerRecords<String, String>(mapRecords);
		Mockito.when(iscPropertiesLoader.getIntegerProperty("consumer.poll.interval")).thenReturn(1000);
		Mockito.when(consumer.poll(1000)).thenReturn(consumerRecords);
		Mockito.doNothing().doThrow(new RuntimeException()).when(consumerFunction).accept("value");
	}

	@Test
	public void testRun() {
		try {
			iscConsumerThread.run();
		} catch (RuntimeException exception) {
			Mockito.verify(consumerFunction, Mockito.times(2)).accept("value");
		}
	}

}
