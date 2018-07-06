package com.pearson.glp.crosscutting.isc.client.async.config;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * The ZookeeperConfigurationTest class.
 * 
 * @author Md Sakib
 *
 */
public class ZookeeperConfigurationTest {
    
    
	/**
     * ZookeeperConfigurationTest instantiate.
     */
    public ZookeeperConfigurationTest() {
        super();
    }
    /**
     * The reference variable of ZookeeperConfiguration. 
     */
    private ZookeeperConfiguration configuration;

    /**
     * Setup and mocking required the object.
     */
    @BeforeClass
    public void setUp() {
        this.configuration = new ZookeeperConfiguration();
    }

    /**
     * The method test the configuration.
     */
    @Test
    private void testZookeeperConfiguration() {
        Assert.assertNotNull(this.configuration);
    }
}
