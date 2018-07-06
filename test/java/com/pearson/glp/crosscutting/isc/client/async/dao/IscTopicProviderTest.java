package com.pearson.glp.crosscutting.isc.client.async.dao;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.pearson.glp.crosscutting.isc.client.async.config.ZookeeperConfiguration;
import com.pearson.glp.crosscutting.isc.client.async.dao.IscTopicProvider;

public class IscTopicProviderTest {

	@Mock
	ZookeeperConfiguration zookeeperConfiguration;

	@InjectMocks
	private IscTopicProvider iscTopicProvider = new IscTopicProvider();

	@BeforeClass
	public void setUp() {
		MockitoAnnotations.initMocks(this);

	}

	@Test
	private void getTopicNameTest() {
		Assert.assertEquals(iscTopicProvider.getTopicName("e1"), "e1_Topic");

	}

	@Test
	private void registerTopicTest() {
		Mockito.doNothing().when(zookeeperConfiguration).createTopic("event1_Topic");
		iscTopicProvider.registerTopic("event1");
		Mockito.verify(zookeeperConfiguration, Mockito.times(1)).createTopic("event1_Topic");
	}

}
