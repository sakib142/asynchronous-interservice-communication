package com.pearson.glp.crosscutting.isc.client.async.service.impl;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearson.glp.crosscutting.isc.client.async.config.IscPropertiesLoader;
import com.pearson.glp.crosscutting.isc.client.async.model.EventMessage;

/**
 * The IscConsumerThread class.
 * 
 * <p>The class consume the message from kafka.
 * 
 * @author Md Sakib
 *
 */
class IscConsumerThread implements Runnable {
    /**
     * IscConsumerThread instantiate.
     */
    public IscConsumerThread() {
        super();
    }

    /**
     * Object of Logger class.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(IscConsumerThread.class);

    /**
     * Name Of topic.
     */
    private String topicName;

    /**
     * The callback function.
     */
    private Consumer<String> callBackFunction;

    /**
     * The kafka consumer.
     */
    private org.apache.kafka.clients.consumer.Consumer<String, String> consumer;

    /**
     * The instance of IscPropertiesLoader.
     */
    private IscPropertiesLoader iscPropertiesLoader;

    /**
     * The parameter constructor instantiate.
     * 
     * @param topicName
     *            name of topic
     * @param callBackFunction
     *            callback function
     * @param consumer
     *            kafkaConsumer object
     * @param iscPropertiesLoader
     *            object
     */
    public IscConsumerThread(String topicName,
            Consumer<String> callBackFunction,
            org.apache.kafka.clients.consumer.Consumer<String, String> consumer,
            IscPropertiesLoader iscPropertiesLoader) {
        this.topicName = topicName;
        this.callBackFunction = callBackFunction;
        this.consumer = consumer;
        this.iscPropertiesLoader = iscPropertiesLoader;
        this.consumer.subscribe(Arrays.asList(this.topicName));
    }

    @Override
    public void run() {
        String consumeMessage;
        while (true) {
            ConsumerRecords<String, String> records = this.consumer.poll(
                this.iscPropertiesLoader.getIntegerProperty("poll.interval"));
            if (records != null
                    && !records.isEmpty()) {
                for (final ConsumerRecord<String, String> record : records) {
                    consumeMessage = this.getMessage(record.value());
                    this.callBackFunction.accept(consumeMessage);
                    logger.info("Receive message:{} , Topic:{} , Key:{} ,"
                            + " Partition:{} , Offset:{} ",
                        record.value(), this.topicName, record.key(),
                        record.partition(), record.offset());

                }
            }
        }
    }

    /**
     * Get message.
     * 
     * @param value
     *            message value
     * @return string payload value
     */
    private String getMessage(String value) {
        Type type = new TypeToken<EventMessage>() {
        }.getType();
        Gson gson = new Gson();
        EventMessage eventMessage = gson.fromJson(value, type);
        return eventMessage.getPayload();
    }
}
