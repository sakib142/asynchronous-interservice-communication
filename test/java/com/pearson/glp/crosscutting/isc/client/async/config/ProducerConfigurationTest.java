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

public class ProducerConfigurationTest {

	@Mock
	private IscPropertiesLoader iscPropertiesLoader;

	@InjectMocks
	private ProducerConfiguration configuration;

	@BeforeClass
	public void set() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(iscPropertiesLoader.getStringProperty("acks")).thenReturn("all");
		Mockito.when(iscPropertiesLoader.getStringProperty("retries")).thenReturn("0");
		Mockito.when(iscPropertiesLoader.getStringProperty("batch.size")).thenReturn("10");
		Mockito.when(iscPropertiesLoader.getStringProperty("linger.ms")).thenReturn("1");
		Mockito.when(iscPropertiesLoader.getStringProperty("key.serializer"))
				.thenReturn("org.apache.kafka.common.serialization.StringSerializer");
		Mockito.when(iscPropertiesLoader.getStringProperty("value.serializer"))
				.thenReturn("org.apache.kafka.common.serialization.StringSerializer");
		Mockito.when(iscPropertiesLoader.getStringProperty("bootstrap.servers")).thenReturn("localhost:9092");
		Mockito.when(iscPropertiesLoader.getStringProperty("security.protocal.config")).thenReturn("SSL");
		Mockito.when(iscPropertiesLoader.getStringProperty("ssl.configuration.flag")).thenReturn("1");
		Mockito.when(iscPropertiesLoader.getStringProperty("ssl.key.password.config")).thenReturn("test1234");
		Mockito.when(iscPropertiesLoader.getStringProperty("ssl.truststore.location.config"))
				.thenReturn("D:/config/kafka.client.truststore.jks");
		Mockito.when(iscPropertiesLoader.getStringProperty("ssl.truststore.password.config")).thenReturn("test1234");
		Mockito.when(iscPropertiesLoader.getStringProperty("ssl.keystore.location.config"))
				.thenReturn("D:/config/kafka.server.keystore.jks");
		Mockito.when(iscPropertiesLoader.getStringProperty("ssl.keystore.password.config")).thenReturn("test1234");
	}

	@Test
	public void testProducerPropertyObject() throws FileNotFoundException, IOException {
		Assert.assertNotNull(configuration.getProperties(), "Producer's propety object cannt be null.");
	}

}
