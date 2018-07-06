package com.pearson.glp.crosscutting.isc.client.async.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.AmazonKinesisException;
import com.amazonaws.services.kinesis.model.DeleteStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.amazonaws.services.kinesis.model.ResourceNotFoundException;
import com.amazonaws.services.kinesis.model.Shard;
import com.amazonaws.services.kinesis.model.StreamDescription;
import com.amazonaws.services.kinesis.model.UpdateShardCountRequest;
import com.pearson.glp.crosscutting.isc.client.async.cache.TopicCache;
import com.pearson.glp.crosscutting.isc.client.async.config.IscPropertiesLoader;
import com.pearson.glp.crosscutting.isc.client.async.dao.StreamInfoDao;
import com.pearson.glp.crosscutting.isc.client.async.model.StreamInformation;
import com.pearson.glp.crosscutting.isc.client.async.service.MessagingService;
import com.pearson.glp.crosscutting.isc.client.async.validation.Validator;

@EnableAsync(proxyTargetClass = true)
public class KinesisMessagingService implements MessagingService {

    /**
     * Default constructor.
     */
    public KinesisMessagingService() {
        super();
    }

    /**
     * Object of Logger class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(KinesisMessagingService.class);

    @Autowired
    private IscPropertiesLoader iscPropLoader;

    @Autowired
    private KinesisStreamOperations KinesisStreamOperations;

    @Autowired
    private AmazonKinesis amazonKinesis;

    @Autowired
    private TopicCache topicChache;

    @Autowired
    private Validator validator;

    @Autowired
    private StreamInfoDao streamInfoDao;

    @Override
    public void register(String eventName) {
        String validatedEventName = this.validateEventName(eventName);
        String streamName =
                this.iscPropLoader.getStringProperty("spring.application.name");
        if (streamName == null) {
            streamName = "dead_stream";
        }
        StreamInformation streamInformation = this.streamInfoDao
                .getStreamInformationByEventName(validatedEventName);
        if (validatedEventName != null
                && streamInformation == null) {
            streamInformation = new StreamInformation();
            streamInformation.setEventName(validatedEventName);
            streamInformation.setStreamName(streamName);
            this.streamInfoDao.create(streamInformation);
        }

    }

    public void createStream(String streamName) {

        this.amazonKinesis.createStream(streamName,
            this.iscPropLoader.getIntegerProperty("shard.count"));

        long startTime = System.currentTimeMillis();
        long endTime = startTime
                + (10
                        * 60 * 1000);

        while (System.currentTimeMillis() < endTime) {
            try {
                Thread.sleep(this.iscPropLoader
                        .getIntegerProperty("stream.create.timeout"));
            } catch (Exception e) {
            }

            try {
                DescribeStreamResult describeStreamResponse =
                        this.amazonKinesis.describeStream(streamName);
                String streamStatus = describeStreamResponse
                        .getStreamDescription().getStreamStatus();
                if (streamStatus.equals("ACTIVE")) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            } catch (ResourceNotFoundException e) {
            }
        }
        if (System.currentTimeMillis() >= endTime) {
            throw new RuntimeException("Stream "
                    + streamName + " never went active");
        }
    }

    public void deleteStream(String streamName) {
        DeleteStreamRequest deleteStreamRequest = new DeleteStreamRequest();
        deleteStreamRequest.setStreamName(streamName);
        this.amazonKinesis.deleteStream(deleteStreamRequest);
    }

    public StreamDescription describeStream(String streamName) {
        StreamDescription streamDescription = null;
        DescribeStreamRequest describeStreamRequest =
                new DescribeStreamRequest();
        describeStreamRequest.setStreamName(streamName);
        try {
            DescribeStreamResult describeStreamResult =

                    this.amazonKinesis.describeStream(describeStreamRequest);
            streamDescription = describeStreamResult.getStreamDescription();
        } catch (ResourceNotFoundException ex) {
            LOGGER.warn("The stream name {}  : {} ", streamName,
                ex.getMessage());
            streamDescription = new StreamDescription();
            streamDescription.setStreamStatus("NOTEXISTS");
        } catch (AmazonKinesisException e) {
            LOGGER.warn("The stream name {}  : {} ", streamName,
                e.getMessage());
            streamDescription = new StreamDescription();
            streamDescription.setStreamStatus("VALIDATIONFAIL");
        }
        return streamDescription;
    }

    public void updateShards(String streamName, Integer targetShardCount) {
        UpdateShardCountRequest countRequest = new UpdateShardCountRequest();
        countRequest.setStreamName(streamName);
        countRequest.setTargetShardCount(targetShardCount);
        this.amazonKinesis.updateShardCount(countRequest);
    }

    @Override
    public void publish(String eventName, String data) {
        String validatedEventName = this.validateEventName(eventName);
        StreamInformation streamInformation = this.streamInfoDao
                .getStreamInformationByEventName(validatedEventName);
        String streamName =
                this.iscPropLoader.getStringProperty("spring.application.name");
        if (streamName == null) {
            streamName = "dead_stream";
        }
        if (validatedEventName == null) {
            validatedEventName =
                    this.iscPropLoader.getStringProperty("default.event.name");
        }

        if (validatedEventName != null
                && streamInformation != null) {

            PutRecordResult produceDataResult =
                    this.amazonKinesis.putRecord(streamName,
                        ByteBuffer.wrap(data.getBytes()), validatedEventName);
            LOGGER.info("shardId :"
                    + produceDataResult.getShardId() + " sequenceNumber :"
                    + produceDataResult.getSequenceNumber());
        } else if (validatedEventName == null) {
            LOGGER.error("Event data could not publish due to eventName"
                    + "   {} validatition failed ",
                validatedEventName);
        } else {
            LOGGER.error("Event data could not publish due to eventName"
                    + "   {} not register !",
                validatedEventName);
        }
    }

    @Override
    public void subscribe(String eventName, Consumer<String> callBackFunction) {
        String validatedEventName = this.validateEventName(eventName);
        StreamInformation streamInformation = this.streamInfoDao
                .getStreamInformationByEventName(validatedEventName);
        if (validatedEventName != null
                && streamInformation != null) {
            Shard shard = this.getShardList(validatedEventName);
            this.KinesisStreamOperations.getRecordsFromStream(
                shard.getShardId(), validatedEventName,
                this.iscPropLoader.getStringProperty("shard.type"),
                streamInformation.getStreamName(), callBackFunction);
        } else if (validatedEventName == null) {
            LOGGER.error(
                "EventName could not subscribe stream due to eventName   {} "
                        + " validatition failed ",
                validatedEventName);
        } else {
            LOGGER.error("Event data could not subscribe due to eventName"
                    + "   {} not register !",
                validatedEventName);
        }
    }

    @Override
    public void register(Set<String> eventNameSet) {
        eventNameSet.forEach(k -> this.register(k));
    }

    @Override
    public void publish(String eventName, String jsonData,
            Consumer<String> callBackFunction) {
        String validatedEventName = this.validateEventName(eventName);
        StreamInformation streamInformation = this.streamInfoDao
                .getStreamInformationByEventName(validatedEventName);
        String streamName =
                this.iscPropLoader.getStringProperty("spring.application.name");
        if (streamName == null) {
            streamName = "dead_stream";
        }

        if (validatedEventName == null) {
            validatedEventName =
                    this.iscPropLoader.getStringProperty("default.event.name");
        }

        if (validatedEventName != null
                && streamInformation != null) {
            PutRecordResult produceDataResult = amazonKinesis.putRecord(
                this.iscPropLoader.getStringProperty("stream.name"),
                ByteBuffer.wrap(jsonData.getBytes()), validatedEventName);
            LOGGER.info("shardId :"
                    + produceDataResult.getShardId() + " sequenceNumber :"
                    + produceDataResult.getSequenceNumber());
            callBackFunction.accept(produceDataResult.getShardId());
        } else if (validatedEventName == null) {
            LOGGER.error("Event data could not publish due to eventName   {} "
                    + " validatition failed ",
                validatedEventName);
            callBackFunction.accept("Event data could not publish on EventName "
                    + eventName + " due to validation failed");
        } else {
            LOGGER.error("Event data could not publish due to eventName"
                    + "   {} not register !",
                validatedEventName);
            callBackFunction.accept("Event data could not publish due to eventName"
                    + eventName + "  not register ");
        }
    }

    @Override
    public void subscribe(Map<String, Consumer<String>> eventCallBackMap) {

        eventCallBackMap.forEach((k, v) -> this.subscribe(k, v));

    }

    /**
     * The method return shard list.
     * 
     * @param partitionKey
     *            value of partition key
     * @return object of shard
     */
    private Shard getShardList(String partitionKey) {
        BigInteger biPartitionKey = this.convertToBigInteger(partitionKey);
        Shard shardDesc = null;
        StreamDescription streamDesc = amazonKinesis
                .describeStream(
                    this.iscPropLoader.getStringProperty("stream.name"))
                .getStreamDescription();
        List<Shard> shards = streamDesc.getShards();
        for (Shard shard : shards) {
            BigInteger startingHashKey = new BigInteger(
                    shard.getHashKeyRange().getStartingHashKey());
            BigInteger endingHashKey =
                    new BigInteger(shard.getHashKeyRange().getEndingHashKey());
            if (startingHashKey.compareTo(biPartitionKey) <= 0
                    && endingHashKey.compareTo(biPartitionKey) >= 0) {
                shardDesc = shard;
                break;
            }
        }
        LOGGER.info("shardList : {}", shardDesc);
        return shardDesc;

    }

    /**
     * The method convert partition key to big integer.
     * 
     * @param partitionKey
     *            value of partition key
     * @return object of BigInteger
     */
    private BigInteger convertToBigInteger(String partitionKey) {
        byte[] partitionBytes;
        byte[] hashBytes;
        BigInteger biPartitionKey = null;
        try {
            partitionBytes = partitionKey.getBytes("UTF-8");

            hashBytes = MessageDigest.getInstance("MD5").digest(partitionBytes);

            biPartitionKey = new BigInteger(1, hashBytes);

        } catch (UnsupportedEncodingException e) {
            LOGGER.info("Execption "
                    + e);
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.info("Execption "
                    + ex);
        }
        return biPartitionKey;
    }

    /**
     * The method used to validate event name.
     * 
     * @param eventName
     *            name of event
     * @return string validated event name
     *
     */
    private String validateEventName(String eventName) {

        try {
            String validatedEventName;
            validatedEventName = this.validator.validate(eventName);
            return validatedEventName;
        } catch (Exception e) {
            LOGGER.error("Exception in event name{}", e.getMessage());
            return null;
        }
    }

}
