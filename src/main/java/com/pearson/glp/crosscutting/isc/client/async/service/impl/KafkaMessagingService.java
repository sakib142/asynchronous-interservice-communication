package com.pearson.glp.crosscutting.isc.client.async.service.impl;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pearson.glp.crosscutting.isc.client.async.config.ConsumerConfiguration;
import com.pearson.glp.crosscutting.isc.client.async.config.IscPropertiesLoader;
import com.pearson.glp.crosscutting.isc.client.async.config.ZookeeperConfiguration;
import com.pearson.glp.crosscutting.isc.client.async.dao.IscTopicProvider;
import com.pearson.glp.crosscutting.isc.client.async.exception.AsyncException;
import com.pearson.glp.crosscutting.isc.client.async.model.EventMessage;
import com.pearson.glp.crosscutting.isc.client.async.service.MessagingService;

/**
 * The KafkaMessagingService class.
 * 
 * @author Md Sakib
 *
 */
public class KafkaMessagingService implements MessagingService{

    /**
     * KafkaMessagingService instantiate.
     */
    public KafkaMessagingService() {
        super();
    }

    /**
     * Object of Logger class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(KafkaMessagingService.class);

    /**
     * Object of Producer .
     */
    @Autowired
    private Producer<String, String> producer;

    /**
     * Object of ConsumerConfiguration .
     */
    @Autowired
    private ConsumerConfiguration consumerConfiguration;

    /**
     * Object of IscTopicProvider .
     */
    @Autowired
    private IscTopicProvider iscTopicProvider;

    /**
     * Object of IscPropertiesLoader .
     */
    @Autowired
    private IscPropertiesLoader iscPropertiesLoader;

    /**
     * Object of ZookeeperConfiguration .
     */
    @Autowired
    private ZookeeperConfiguration zookeeperConfiguration;

    /**
     * The method used to register event.
     * 
     * @param eventNameSet
     *            name of event
     */
    @Override
    public void register(Set<String> eventNameSet) {
        try {
            this.iscTopicProvider.registerTopic(eventNameSet);
        } catch (AsyncException e) {
            LOGGER.error("Error in registerTopic {}", e);
        }

    }

    /**
     * The method used to register event.
     * 
     * @param eventName
     *            name of event
     */
    @Override
    public void register(String eventName) {
        try {
            this.iscTopicProvider.registerTopic(eventName);
        } catch (AsyncException e) {
            LOGGER.error("Error in register topic {}", e);
        }

    }

    /**
     * The method used to subscribe the event.
     * 
     * @param eventName
     *            name of event
     * @param callBackFunction
     *            callback function
     */
    @Override
    public void subscribe(String eventName, Consumer<String> callBackFunction) {

        String topicname = null;
        try {
            topicname = this.iscTopicProvider.getTopicFromEventName(eventName);
        } catch (AsyncException e) {
            LOGGER.error("Error in subscribe topic {}", e);
        }
        if (this.zookeeperConfiguration.isTopicExist(topicname)) {

            IscConsumerThread consumerThread =
                    new IscConsumerThread(topicname, callBackFunction,
                            this.consumerConfiguration.getKafkaConsumer(),
                            this.iscPropertiesLoader);

            Thread thread = new Thread(consumerThread);
            thread.start();
        } else {
            LOGGER.error("Error while subscribing.Event name does not exist");

        }
    }

    /**
     * The method used to subscribe event.
     * 
     * @param eventCallBackMap
     *            the map of event
     */
    @Override
    public void subscribe(Map<String, Consumer<String>> eventCallBackMap) {

        String topicname = "";
        for (Map.Entry<String, Consumer<String>> entry : eventCallBackMap
                .entrySet()) {
            try {
                topicname = this.iscTopicProvider
                        .getTopicFromEventName(entry.getKey());
            } catch (AsyncException e) {
                LOGGER.error("Error in getTopicFromEventName {}", e);
            }

            if (this.zookeeperConfiguration.isTopicExist(topicname)) {

                IscConsumerThread consumerThread =
                        new IscConsumerThread(topicname, entry.getValue(),
                                this.consumerConfiguration.getKafkaConsumer(),
                                this.iscPropertiesLoader);

                Thread thread = new Thread(consumerThread);
                thread.start();
            } else {
                LOGGER.error(
                    "Error while subscribing.Event name does not exist");
            }
        }
    }

    /**
     * The method used to publish message.
     * 
     * @param eventName
     *            name of event
     * @param jsonData
     *            publish json data
     * @param callBackFunction
     *            callback function
     */
    @Override
    public void publish(String eventName, final String jsonData,
            final Consumer<String> callBackFunction) {
        this.publishEvent(eventName, jsonData, callBackFunction);
    }

    /**
     * The method used to publish message.
     * 
     * @param eventName
     *            name of event
     * @param jsonData
     *            publish json data
     */
    @Override
    public void publish(String eventName, final String jsonData) {
        this.publishEvent(eventName, jsonData, null);
    }

    /**
     * The method used to publish event.
     * 
     * @param eventName
     *            name of event
     * @param jsonData
     *            publish json data
     * @param callBackFunction
     *            callback function
     */
    private void publishEvent(String eventName, final String jsonData,
            final Consumer<String> callBackFunction) {
        String topicname = null;
        try {
            topicname = this.iscTopicProvider.getTopicNameFromCache(eventName);
        } catch (AsyncException e) {
            LOGGER.error("Error in getTopicNameFromCache {}", e);
        }
        String produceMessage = this.prepareMessage(topicname, jsonData);
        LOGGER.info("Produce message on topic {} , message is {}", topicname,
            jsonData);
        if (callBackFunction == null) {
            this.producer.send(
                new ProducerRecord<String, String>(topicname, produceMessage));
        } else {
            this.producer.send(
                new ProducerRecord<String, String>(topicname, produceMessage),
                new Callback() {

                    @Override
                    public void onCompletion(RecordMetadata metadata,
                            Exception exception) {
                        if (exception != null) {
                            LOGGER.error("Error in send message {}", exception);
                        }

                        callBackFunction.accept(metadata.topic());

                    }
                });
        }

    }

    /**
     * Prepare message.
     * 
     * @param topicName
     *            name of topic
     * @param jsonData
     *            produce message
     * @return string produce message
     */
    private String prepareMessage(String topicName, String jsonData) {
        EventMessage eventMessage = new EventMessage();
        eventMessage.setId(UUID.randomUUID().toString());
        eventMessage.setEventName(topicName);
        eventMessage.setTimestamp(String.valueOf(System.currentTimeMillis()));
        eventMessage.setPayload(jsonData);
        Gson gson = new Gson();
        return gson.toJson(eventMessage);
    }

}
