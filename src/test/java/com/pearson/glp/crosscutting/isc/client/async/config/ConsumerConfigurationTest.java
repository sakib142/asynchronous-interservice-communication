package com.pearson.glp.crosscutting.isc.client.async.config;

import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * The ConsumerConfigurationTest class.
 * 
 * @author Md Sakib
 *
 */
public class ConsumerConfigurationTest {

    /**
     * ConsumerConfigurationTest instantiate.
     */
    public ConsumerConfigurationTest() {
        super();
    }
    /**
     * The constant value of GROUPID.
     */
    private static final String GROUPID = "group.id";
    /**
     * The constant value of EAUTOCOMMIT . 
     */
    private static final String EAUTOCOMMIT = "enable.auto.commit";
    /**
     * The constant value of ACIMS . 
     */
    private static final String ACIMS = "auto.commit.interval.ms";
    /**
     * The constant value of STMS . 
     */
    private static final String STMS = "session.timeout.ms";
    /**
     * The constant value of KDESER . 
     */
    private static final String KDESER = "key.deserializer";
    /**
     * The constant value of VDESER . 
     */
    private static final String VDESER = "value.deserializer";
    /**
     * The constant value of BOOT_SERV . 
     */
    private static final String BOOT_SERV = "bootstrap.servers";
    /**
     * The constant value of POLL_INT . 
     */
    private static final String POLL_INT = "poll.interval";
    /**
     * The constant value of SSL_CONF_FLAG . 
     */
    private static final String SSL_CONF_FLAG = "ssl.configuration.flag";
    /**
     * The constant value of SP_CONFIG . 
     */
    private static final String SP_CONFIG = "security.protocal.config";
    /**
     * The constant value of SKPASS . 
     */
    private static final String SKPASS = "ssl.key.password.config";
    /**
     * The constant value of SCLOC . 
     */
    private static final String SCLOC = "ssl.truststore.location.config";
    /**
     * The constant value of STPASS . 
     */
    private static final String STPASS = "ssl.truststore.password.config";
    /**
     * The constant value of SCKL . 
     */
    private static final String SCKL = "ssl.keystore.location.config";
    /**
     * The constant value of SKPC . 
     */
    private static final String SKPC = "ssl.keystore.password.config";
    /**
     * The constant value of KDSERV . 
     */
    private static final String KDSERV =
            "org.apache.kafka.common.serialization.StringDeserializer";
    /**
     * The constant value of VDESERV . 
     */
    private static final String VDESERV =
            "org.apache.kafka.common.serialization.StringDeserializer";
    /**
     * The constant value of BOOT_IP . 
     */
    private static final String BOOT_IP = "localhost:9092";
    /**
     * The constant value of SCLOCV . 
     */
    private static final String SCLOCV =
            "D:/config/kafka.client.truststore.jks";
    /**
     * The constant value of SCKLV . 
     */
    private static final String SCKLV = "D:/config/kafka.server.keystore.jks";
    /**
     * The constant value of CONF_RES . 
     */
    private static final String CONF_RES =
            "Consumer's propety object cannt be null.";
    /**
     * The mock object of IscPropertiesLoader.
     */
    @Mock
    private IscPropertiesLoader iPLoader;

    /**
     * Inject mock object of ConsumerConfiguration.
     */
    @InjectMocks
    private ConsumerConfiguration consumerConf;

    /**
     * Setup and mocking required the object.
     */
    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(this.iPLoader.getStringProperty(GROUPID)).thenReturn("group01");
        when(this.iPLoader.getStringProperty(EAUTOCOMMIT)).thenReturn("true");
        when(this.iPLoader.getStringProperty(ACIMS)).thenReturn("1000");
        when(this.iPLoader.getStringProperty(STMS)).thenReturn("30000");
        when(this.iPLoader.getStringProperty(KDESER)).thenReturn(KDSERV);
        when(this.iPLoader.getStringProperty(VDESER)).thenReturn(VDESERV);
        when(this.iPLoader.getStringProperty(BOOT_SERV)).thenReturn(BOOT_IP);
        when(this.iPLoader.getStringProperty(POLL_INT)).thenReturn("1000");
        when(this.iPLoader.getStringProperty(SSL_CONF_FLAG)).thenReturn("1");
        when(this.iPLoader.getStringProperty(SP_CONFIG)).thenReturn("SSL");
        when(this.iPLoader.getStringProperty(SKPASS)).thenReturn("test1234");
        when(this.iPLoader.getStringProperty(SCLOC)).thenReturn(SCLOCV);
        when(this.iPLoader.getStringProperty(STPASS)).thenReturn("test1234");
        when(this.iPLoader.getStringProperty(SCKL)).thenReturn(SCKLV);
        when(this.iPLoader.getStringProperty(SKPC)).thenReturn("test1234");
    }

    /**
     * The method used to test ConsumerConfiguration null or not.
     */
    @Test
    public void testConsumerConfigurationObject() {

        assertNotNull(this.consumerConf.getProperties(), CONF_RES);

    }

}
