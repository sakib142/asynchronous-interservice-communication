package com.pearson.glp.crosscutting.isc.client.async.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.pearson.glp.crosscutting.isc.client.async.config.ConsumerConfiguration;
import com.pearson.glp.crosscutting.isc.client.async.config.IscPropertiesLoader;
import com.pearson.glp.crosscutting.isc.client.async.dao.IscTopicProvider;

public class IscAsyncClientTest {

	@Mock
	private IscTopicProvider iscTopicProvider;

	@Mock
	private KafkaProducer<String, String> producer;

	@Mock
	org.apache.kafka.clients.consumer.Consumer<String, String> consumer;

	@Mock
	private IscPropertiesLoader iscPropertiesLoader;

	@Mock
	private ConsumerConfiguration consumerConfiguration;

	@InjectMocks
	private IscAsyncClient iscAsyncClient;

	Consumer<String> consumerFunction;

	@BeforeClass
	public void beforeClass() {
		MockitoAnnotations.initMocks(this);
	}

	@BeforeMethod
	public void beforeMethod() {
		Mockito.doNothing().when(iscTopicProvider).registerTopic("TEST");
		Mockito.when(iscTopicProvider.getTopicName("TEST")).thenReturn("TEST_TOPIC");
		consumerFunction = x -> System.out.println(x);

	}

	@Test
	public void testRegister() {
		String eventName = "testEvent";
		iscAsyncClient.register(eventName);
		verify(iscTopicProvider, times(1)).registerTopic(eventName);

	}

	@Test
	public void testSubscribe() throws FileNotFoundException, IOException {
		MockConsumer<String, String> consumer = new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST);
		Mockito.when(consumerConfiguration.getKafkaConsumer()).thenReturn(consumer);
		// creates a decorator spying on the method calls of the real instance
		IscConsumerThread launcher = Mockito
				.spy(new IscConsumerThread("", consumerFunction, consumer, iscPropertiesLoader));
		Thread thread = new Thread(launcher);
		thread.start();
		iscAsyncClient.subscribe("", consumerFunction);
		verify(iscTopicProvider, times(1)).getTopicName("");
	}

	@Test
	public void testPublishWithConsumerFunctionNotNull() {
		iscAsyncClient.publishWithConsumerFunction("TEST", "TEST_JSON", consumerFunction);
		verify(producer, times(1)).send(Mockito.any(ProducerRecord.class), Mockito.any(Callback.class));
	}

	@Test
	public void testPublishWithoutConsumerFunction() {
		iscAsyncClient.publish("TEST", "TEST_JSON");
		verify(producer, times(1)).send(Mockito.any(ProducerRecord.class));
	}

}
