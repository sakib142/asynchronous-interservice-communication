package com.pearson.glp.crosscutting.isc.client.async.config;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * The ProducerConfiguration class.
 * 
 *<p>Create the KafkaProducer instance and set the properties.
 * 
 * @author Md Sakib
 *
 */
@Component
@Profile("kafka")
public class ProducerConfiguration {

	/**
	 * ProducerConfiguration instantiate. 
	 */
    public ProducerConfiguration() {
        super();
    }

    /**
     * The instance of IscPropertiesLoader class.
     */
    @Autowired
    private IscPropertiesLoader iscPropertiesLoader;

    /**
     * Gets KafkaProducer .
     * 
     *<p>Set the properties in KafkaProducer.
     * 
     * @return the KafkaProducer object
     */
    @Bean
    public Producer<String, String> getKafkaProducer() throws IOException {
        return new KafkaProducer<>(this.getProperties());
    }

    /**
     * The method return the KafkaProducer properties.
     * 
     *<p>And read the properties from  property file.
     * 
     * @return the properties object
     */
    public Properties getProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                this.iscPropertiesLoader
                        .getStringProperty("bootstrap.servers"));
        props.put(ProducerConfig.ACKS_CONFIG,
                this.iscPropertiesLoader.getStringProperty("acks"));
        props.put(ProducerConfig.RETRIES_CONFIG,
                this.iscPropertiesLoader.getStringProperty("retries"));
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                this.iscPropertiesLoader.getStringProperty("key.serializer"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                this.iscPropertiesLoader.getStringProperty("value.serializer"));
        this.setSslConfig(props);
        return props;

    }

    /**
     * The method set the ssl properties.
     * 
     *<p>And read the properties from  property file.
     * 
     * @param props
     *            Properties object
     */
    private void setSslConfig(Properties props) {
        if (this.iscPropertiesLoader
                .getStringProperty("ssl.configuration.flag") != null) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,
                    this.iscPropertiesLoader
                            .getStringProperty("security.protocal.config"));
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
                    this.iscPropertiesLoader.getStringProperty(
                            "ssl.truststore.location.config"));
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG,
                    this.iscPropertiesLoader.getStringProperty(
                            "ssl.truststore.password.config"));
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,
                    this.iscPropertiesLoader
                            .getStringProperty("ssl.keystore.location.config"));
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG,
                    this.iscPropertiesLoader
                            .getStringProperty("ssl.keystore.password.config"));
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG,
                    this.iscPropertiesLoader
                            .getStringProperty("ssl.key.password.config"));
        }

    }

}
