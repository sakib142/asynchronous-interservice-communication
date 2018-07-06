package com.pearson.glp.crosscutting.isc.client.async.service;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.pearson.glp.crosscutting.isc.client.async.service.MessagingService;
@EnableAsync(proxyTargetClass=true)
@Component
public class IscAsyncClient {

    private MessagingService iscClient;

    @Autowired
    public void setIscClient(MessagingService client) {

        this.iscClient = client;
    }

    public void register(String eventName) {
        this.iscClient.register(eventName);
    }

    public void register(Set<String> eventNameSet) {
        this.iscClient.register(eventNameSet);
    }

    public void publish(String eventName, final String jsonData) {
        this.iscClient.publish(eventName, jsonData);
    }

    public void publish(String eventName, final String jsonData,
            final Consumer<String> callBackFunction) {
        this.iscClient.publish(eventName, jsonData, callBackFunction);
    }

    public void subscribe(String eventName, Consumer<String> callBackFunction) {

        this.iscClient.subscribe(eventName, callBackFunction);
    }

    public void subscribe(Map<String, Consumer<String>> eventCallBackMap) {

        this.iscClient.subscribe(eventCallBackMap);
    }

}
