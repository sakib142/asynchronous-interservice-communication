package com.pearson.glp.crosscutting.isc.client.async.config;

import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.pearson.glp.crosscutting.isc.client.async.service.impl.KinesisMessagingService;

/**
 * The KinesisConfigurationTest class.
 * 
 * @author Md Sakib
 *
 */
public class KinesisConfigurationTest {

    /**
     * ConsumerConfigurationTest instantiate.
     */
    public KinesisConfigurationTest() {
        super();
    }
    /**
     * The mock object of IscPropertiesLoader.
     */
    @Mock
    private IscPropertiesLoader iPLoader;

    /**
     * Inject mock object of KinesisConfiguration.
     */
    @InjectMocks
    private KinesisConfiguration kinesisConfiguration;
    
    /**
     * Setup and mocking required the object.
     */
    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(this.iPLoader.getStringProperty("aws.secret.key")).thenReturn("test");
        when(this.iPLoader.getStringProperty("aws.access.key")).thenReturn("test");
        when(this.iPLoader.getStringProperty("aws.regions")).thenReturn("us-west-2");
        when(this.iPLoader
                    .getStringProperty("secret.key")).thenReturn("test");
    }

    /**
     * The method used to test AmazonKinesis null or not.
     */
    @Test
    public void testGetAmazonKinesis() {

       AmazonKinesis amazonKinesis = kinesisConfiguration.getAmazonKinesis();
       Assert.assertNotNull(amazonKinesis);
       
    }
    
    /**
     * The method used to test KinesisMessagingService null or not.
     */
    @Test
    public void testGetIscAsyncClient() {

        KinesisMessagingService kinesisMessagingService =
                (KinesisMessagingService) kinesisConfiguration
                        .getIscAsyncClient();
       Assert.assertNotNull(kinesisMessagingService);
    }

}
