package com.pearson.glp.crosscutting.isc.client.async.config;

import java.util.Properties;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.pearson.glp.crosscutting.isc.client.async.service.MessagingService;
import com.pearson.glp.crosscutting.isc.client.async.service.impl.KafkaMessagingService;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;

/**
 * The ZookeeperConfiguration class.
 * 
 * <p>This class create the topics in kafka.
 * 
 * @author Md Sakib
 *
 */
@Component
@ComponentScan(
        value = "com.pearson.glp.crosscutting.isc.client.async.service.impl")
@Profile("kafka")
public class ZookeeperConfiguration {
    /**
     * ZookeeperConfiguration instantiate.
     */
    public ZookeeperConfiguration() {
        super();
    }

    /**
     * Object of Logger class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ZookeeperConfiguration.class);

    /**
     * The instance of IscPropertiesLoader class.
     */
    @Autowired
    private IscPropertiesLoader iscPropertiesLoader;
    private static final String ZKHOST = "zookeeper.hosts";
    private static final String SESSION_TIMEOUT_IN_MS =
            "session.time.out.in.ms";
    private static final String CONNECTION_TIMEOUT_IN_MS =
            "connection.time.out.in.ms";
    private static final String NO_OF_PARTITIONS = "no.of.partitions";

    /**
     * The instance create.
     * 
     * @return object of MessagingService
     */
    @Bean
    public MessagingService getIscAsyncClient() {
        return new KafkaMessagingService();

    }
    
    /**
     * The method used to create topic.
     * 
     * @param topicName
     *            name of topic
     */
    public void createTopic(String topicName) {

        String zkhost = this.iscPropertiesLoader.getStringProperty(ZKHOST);
        ZkClient zkClient = new ZkClient(zkhost,
                this.iscPropertiesLoader
                        .getIntegerProperty(SESSION_TIMEOUT_IN_MS),
                this.iscPropertiesLoader.getIntegerProperty(
                    CONNECTION_TIMEOUT_IN_MS),
                ZKStringSerializer$.MODULE$);
        ZkConnection zkconnection = new ZkConnection(zkhost);
        ZkUtils zkUtils =
                new ZkUtils(zkClient, zkconnection, false);

        Properties topicConfiguration = new Properties();

        AdminUtils.createTopic(zkUtils, topicName,
            this.iscPropertiesLoader.getIntegerProperty(NO_OF_PARTITIONS),
            this.iscPropertiesLoader.getIntegerProperty(NO_OF_PARTITIONS),
            topicConfiguration);

        zkUtils.close();
        try {
            zkconnection.close();
        } catch (InterruptedException e) {
           LOGGER.error("Exception closing ZkConnection : {}",e);
        }

        LOGGER.info("  Topic name {}   created ", topicName);

    }

    /**
     * Check topic name exist or not.
     * 
     * @param topicName
     *            name of topic
     * @return boolean true / false
     */
    public Boolean isTopicExist(String topicName) {
        String zkhost = this.iscPropertiesLoader.getStringProperty(ZKHOST);
        ZkClient zkClient = new ZkClient(zkhost,
                this.iscPropertiesLoader
                        .getIntegerProperty(SESSION_TIMEOUT_IN_MS),
                this.iscPropertiesLoader.getIntegerProperty(
                    CONNECTION_TIMEOUT_IN_MS),
                ZKStringSerializer$.MODULE$);
        ZkConnection zkconnection = new ZkConnection(zkhost);
        ZkUtils zkUtils =
                new ZkUtils(zkClient, zkconnection, false);
        Boolean isExists = AdminUtils.topicExists(zkUtils, topicName);
        
        try {
            zkconnection.close();
        } catch (InterruptedException e) {
           LOGGER.error("Exception closing ZkConnection : {}",e);
        }
        zkUtils.close();
        return isExists;
    }

}
