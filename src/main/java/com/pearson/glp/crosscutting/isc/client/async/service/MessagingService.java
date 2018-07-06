
package com.pearson.glp.crosscutting.isc.client.async.service;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface MessagingService {

    public void register(String eventName);

    public void register(Set<String> eventNameSet);

    public void publish(String eventName, final String jsonData);

    public void publish(String eventName, final String jsonData,
            final Consumer<String> callBackFunction);

    public void subscribe(String eventName, Consumer<String> callBackFunction);

    public void subscribe(Map<String, Consumer<String>> eventCallBackMap);

}
