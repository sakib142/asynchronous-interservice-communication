package com.pearson.glp.crosscutting.isc.client.async.config;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ConsumerConfigurationTest {

	@Mock
	private IscPropertiesLoader iscPropertiesLoader;

	@InjectMocks
	private ConsumerConfiguration configuration;

	@BeforeClass
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(iscPropertiesLoader.getStringProperty("group.id")).thenReturn("group01");
		Mockito.when(iscPropertiesLoader.getStringProperty("enable.auto.commit")).thenReturn("true");
		Mockito.when(iscPropertiesLoader.getStringProperty("auto.commit.interval.ms")).thenReturn("1000");
		Mockito.when(iscPropertiesLoader.getStringProperty("session.timeout.ms")).thenReturn("30000");
		Mockito.when(iscPropertiesLoader.getStringProperty("key.deserializer"))
				.thenReturn("org.apache.kafka.common.serialization.StringDeserializer");
		Mockito.when(iscPropertiesLoader.getStringProperty("value.deserializer"))
				.thenReturn("org.apache.kafka.common.serialization.StringDeserializer");
		Mockito.when(iscPropertiesLoader.getStringProperty("bootstrap.servers")).thenReturn("localhost:9092");
		Mockito.when(iscPropertiesLoader.getStringProperty("poll.interval")).thenReturn("1000");
		Mockito.when(iscPropertiesLoader.getStringProperty("ssl.configuration.flag")).thenReturn("1");
		Mockito.when(iscPropertiesLoader.getStringProperty("security.protocal.config")).thenReturn("SSL");
		Mockito.when(iscPropertiesLoader.getStringProperty("ssl.key.password.config")).thenReturn("test1234");
		Mockito.when(iscPropertiesLoader.getStringProperty("ssl.truststore.location.config"))
				.thenReturn("D:/config/kafka.client.truststore.jks");
		Mockito.when(iscPropertiesLoader.getStringProperty("ssl.truststore.password.config")).thenReturn("test1234");
		Mockito.when(iscPropertiesLoader.getStringProperty("ssl.keystore.location.config"))
				.thenReturn("D:/config/kafka.server.keystore.jks");
		Mockito.when(iscPropertiesLoader.getStringProperty("ssl.keystore.password.config")).thenReturn("test1234");
	}

	@Test
	public void testConsumerConfigurationObject() throws FileNotFoundException, IOException {
		Assert.assertNotNull(configuration.getProperties(), "Consumer's propety object cannt be null.");
	}

}
