package com.pearson.glp.crosscutting.isc.client.async.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.GetRecordsRequest;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest;
import com.amazonaws.services.kinesis.model.GetShardIteratorResult;
import com.amazonaws.services.kinesis.model.Record;
import com.pearson.glp.crosscutting.isc.client.async.config.IscPropertiesLoader;
import com.pearson.glp.crosscutting.isc.client.async.service.StreamOperations;

@Repository
@Profile("kinesis")
public class KinesisStreamOperations implements StreamOperations {

    public KinesisStreamOperations() {
        super();
    }

    /**
     * Object of Logger class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(KinesisStreamOperations.class);

    @Autowired
    private IscPropertiesLoader iscPropLoader;

    @Autowired
    private AmazonKinesis amazonKinesis;

    @Override
    @Async
    public void getRecordsFromStream(String shardId, String eventName,
            String shardType, String streamName,
            Consumer<String> callBackFunction) {

        GetShardIteratorRequest shardItrRequest = new GetShardIteratorRequest();
        shardItrRequest.setStreamName(streamName);
        shardItrRequest.setShardId(shardId);

        shardItrRequest.setShardIteratorType(shardType);

        GetShardIteratorResult shardResult =
                this.amazonKinesis.getShardIterator(shardItrRequest);
        String shardIterator = shardResult.getShardIterator();
        while (StringUtils.hasText(shardIterator)) {
            GetRecordsRequest getRecordsRequest = new GetRecordsRequest();
            getRecordsRequest.setShardIterator(shardIterator);
            getRecordsRequest.setLimit(1000);
            shardIterator = this.processStreamRecords(getRecordsRequest,
                eventName, callBackFunction).getNextShardIterator();
        }
    }

    private GetRecordsResult processStreamRecords(
            GetRecordsRequest getRecordsRequest, String eventName,
            Consumer<String> callBackFunction) {

        GetRecordsResult getRecordsResult =
                this.amazonKinesis.getRecords(getRecordsRequest);
        List<Record> records = getRecordsResult.getRecords();

        for (Record record : records) {
            String data = null;

            try {
                data = new String(record.getData().array(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOGGER.info("Exception : {}", e);
            }

            if (record.getPartitionKey().equals(eventName)) {
                LOGGER.info(
                    "partition key   : {} , SequenceNumber  : {} , Data : {}",
                    record.getPartitionKey(), record.getSequenceNumber(), data);
                callBackFunction.accept(data);

            }
        }
        try {
            Thread.sleep(
                this.iscPropLoader.getIntegerProperty("stream.sleep.time"));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
        return getRecordsResult;
    }

}