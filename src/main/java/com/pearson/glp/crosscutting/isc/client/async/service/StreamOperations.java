
package com.pearson.glp.crosscutting.isc.client.async.service;

import java.util.function.Consumer;

public interface StreamOperations {

    void getRecordsFromStream(String shardId, String eventName,
            String shardType, String streamName,
            Consumer<String> callBackFunction);
}
