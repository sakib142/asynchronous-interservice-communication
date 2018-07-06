package com.pearson.glp.crosscutting.isc.client.async.config;

import org.junit.Assert;
import org.mockito.InjectMocks;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class IscPropertiesLoaderTest {

	@InjectMocks
	private IscPropertiesLoader configuration;

	@Test
	private void propertySourcesPlaceholderConfigurerTest() {
		Assert.assertNotNull("IscPropertiesLoader object cannt null.",
				configuration.propertySourcesPlaceholderConfigurer());
	}

}
