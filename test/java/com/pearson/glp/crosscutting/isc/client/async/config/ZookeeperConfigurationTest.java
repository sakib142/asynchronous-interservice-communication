package com.pearson.glp.crosscutting.isc.client.async.config;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ZookeeperConfigurationTest {

	private ZookeeperConfiguration configuration;

	@BeforeClass
	public void setUp() {
		configuration = new ZookeeperConfiguration();
	}
}
