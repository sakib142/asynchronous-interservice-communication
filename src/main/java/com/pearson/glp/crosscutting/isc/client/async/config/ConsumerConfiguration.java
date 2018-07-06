
package com.pearson.glp.crosscutting.isc.client.async.config;

import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * The ConsumerConfiguration class.
 * 
 * <p>
 * Create the KafkaConsumer instance and set the properties.
 * 
 * @author Md Sakib
 *
 */
@Component
@Profile("kafka")
public class ConsumerConfiguration {

    /**
     * ConsumerConfiguration instantiate.
     */
    public ConsumerConfiguration() {
        super();
    }

    /**
     * The instance of IscPropertiesLoader class.
     */
    @Autowired
    private IscPropertiesLoader iscPropertiesLoader;

    /**
     * Gets KafkaConsumer .
     * 
     * @return the KafkaConsumer object
     */
    public Consumer<String, String> getKafkaConsumer() {
        return new KafkaConsumer<>(this.getProperties());
    }


    /**
     * The method return the KafkaConsumer properties.
     * 
     * @return The properties object
     */
    public Properties getProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
            this.iscPropertiesLoader.getStringProperty("group.id"));
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
            this.iscPropertiesLoader.getStringProperty("enable.auto.commit"));
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
            this.iscPropertiesLoader
                    .getStringProperty("auto.commit.interval.ms"));
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,
            this.iscPropertiesLoader.getStringProperty("session.timeout.ms"));
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            this.iscPropertiesLoader.getStringProperty("key.deserializer"));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            this.iscPropertiesLoader.getStringProperty("value.deserializer"));
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            this.iscPropertiesLoader.getStringProperty("bootstrap.servers"));
        this.setSslConfig(props);
        return props;
    }

    /**
     * The Method set the ssl properties.
     * 
     * @param props
     *            the property object
     */
    private void setSslConfig(Properties props) {
        if (this.iscPropertiesLoader
                .getStringProperty("ssl.configuration.flag") != null) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,
                this.iscPropertiesLoader
                        .getStringProperty("security.protocal.config"));
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG,
                this.iscPropertiesLoader
                        .getStringProperty("ssl.key.password.config"));
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
                this.iscPropertiesLoader
                        .getStringProperty("ssl.truststore.location.config"));
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG,
                this.iscPropertiesLoader
                        .getStringProperty("ssl.truststore.password.config"));
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,
                this.iscPropertiesLoader
                        .getStringProperty("ssl.keystore.location.config"));
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG,
                this.iscPropertiesLoader
                        .getStringProperty("ssl.keystore.password.config"));
        }
    }
}
