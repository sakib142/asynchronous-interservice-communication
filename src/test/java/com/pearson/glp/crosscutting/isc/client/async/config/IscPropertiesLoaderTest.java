package com.pearson.glp.crosscutting.isc.client.async.config;

import static org.junit.Assert.assertNotNull;

import org.mockito.InjectMocks;
import org.testng.annotations.Test;

/**
 * The IscPropertiesLoaderTest class.
 * 
 * @author Md Sakib
 *
 */
public class IscPropertiesLoaderTest {

    
    /**
     * IscPropertiesLoaderTest instantiate.
     */
    public IscPropertiesLoaderTest() {
        super();
    }

    /**
     * Inject mock object of IscPropertiesLoader.
     */
    @InjectMocks
    private IscPropertiesLoader conf;

    /**
     * The method test property object null or not.
     */
    @Test
    private void propertySourcesPlaceholderConfigurerTest() {
        assertNotNull("IscPropertiesLoader object cannt null.",
                this.conf.propertySourcesPlaceholderConfigurer());

    }

}
