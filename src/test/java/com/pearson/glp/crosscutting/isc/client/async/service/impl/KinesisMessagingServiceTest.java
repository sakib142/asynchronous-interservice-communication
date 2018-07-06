package com.pearson.glp.crosscutting.isc.client.async.service.impl;

import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.AmazonKinesisException;
import com.amazonaws.services.kinesis.model.CreateStreamResult;
import com.amazonaws.services.kinesis.model.DeleteStreamRequest;
import com.amazonaws.services.kinesis.model.DeleteStreamResult;
import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.HashKeyRange;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.amazonaws.services.kinesis.model.ResourceNotFoundException;
import com.amazonaws.services.kinesis.model.SequenceNumberRange;
import com.amazonaws.services.kinesis.model.Shard;
import com.amazonaws.services.kinesis.model.StreamDescription;
import com.amazonaws.services.kinesis.model.UpdateShardCountRequest;
import com.amazonaws.services.kinesis.model.UpdateShardCountResult;
import com.pearson.glp.crosscutting.isc.client.async.cache.TopicCache;
import com.pearson.glp.crosscutting.isc.client.async.config.IscPropertiesLoader;
import com.pearson.glp.crosscutting.isc.client.async.dao.StreamInfoDao;
import com.pearson.glp.crosscutting.isc.client.async.exception.AsyncException;
import com.pearson.glp.crosscutting.isc.client.async.model.StreamInformation;
import com.pearson.glp.crosscutting.isc.client.async.validation.Validator;

/**
 * The KinesisMessagingServiceTest class.
 * 
 * @author Md Sakib
 *
 */
public class KinesisMessagingServiceTest {

    /**
     * KinesisMessagingServiceTest instantiate.
     */
    public KinesisMessagingServiceTest() {
        super();
    }

    private final static String EVENT_NAME = "abc";

    private final static String STREAM_NAME = "test";

    /**
     * The mock object of IscPropertiesLoader.
     */
    @Mock
    private IscPropertiesLoader iPLoader;

    /**
     * The mock object of KinesisStreamOperations.
     */
    @Mock
    private KinesisStreamOperations KinesisStreamOperations;

    /**
     * The mock object of AmazonKinesis.
     */
    @Mock
    private AmazonKinesis amazonKinesis;

    /**
     * The mock object of TopicCache.
     */
    @Mock
    private TopicCache topicChache;

    /**
     * The mock object of Validator.
     */
    @Mock
    private Validator validator;

    /**
     * The mock object of StreamInfoDao.
     */
    @Mock
    private StreamInfoDao streamInfoDao;
    /**
     * Inject mock object of KinesisMessagingService.
     */
    @InjectMocks
    private KinesisMessagingService kinesisMessagingService;

    private Map<String, String> map;

    private DescribeStreamResult result;

    private StreamInformation streamInformation;

    private Consumer<String> callBackFunction = x -> {
        System.out.println("Consume message : "
                + x);
    };

    /**
     * Setup and mocking required the object.
     * 
     * @throws AsyncException
     *             custom exception
     */
    @BeforeClass
    public void setUp() throws AsyncException {
        MockitoAnnotations.initMocks(this);
        when(this.iPLoader.getStringProperty("stream.name")).thenReturn("abc");
        when(this.iPLoader.getStringProperty("default.event.name"))
                .thenReturn("abc");
        when(this.iPLoader.getStringProperty("shard.type"))
                .thenReturn("LATEST");
        when(this.iPLoader.getStringProperty("spring.application.name"))
                .thenReturn("isc_demo");
        when(this.streamInfoDao.getStreamInformationByEventName("abc"))
                .thenReturn(streamInformation);
        when(this.validator.validate("abc")).thenReturn("abc");

    }

    /**
     * Setup before execute method.
     */
    @BeforeMethod
    public void beforeMethod() {
        Shard shards = new Shard();
        HashKeyRange hashKeyRange = new HashKeyRange();
        hashKeyRange
                .setStartingHashKey("170141183460469231731687303715884105728");
        hashKeyRange
                .setEndingHashKey("340282366920938463463374607431768211455");
        shards.setHashKeyRange(hashKeyRange);
        SequenceNumberRange numberRange = new SequenceNumberRange();
        numberRange.setStartingSequenceNumber(
            "49575019184441522000784840241236977236196116668813934610");
        shards.setSequenceNumberRange(numberRange);
        shards.setShardId("shardId-000000000001");
        Collection<Shard> collection = new HashSet<>();
        collection.add(shards);
        StreamDescription description = new StreamDescription();
        description.setStreamName(EVENT_NAME);
        description.setShards(collection);
        description.setStreamStatus("ACTIVE");
        this.result = new DescribeStreamResult();
        this.result.setStreamDescription(description);
        this.map = new HashMap<>();
        this.map.put(EVENT_NAME, "shardId-000000000001");

    }

    /**
     * The method used to test register event name.
     * 
     * @throws AsyncException
     *             manual exception
     */
    @Test
    public void testRegister() throws AsyncException {
        StreamInformation streamInformation = new StreamInformation();
        when(this.topicChache.getRegisterMap()).thenReturn(map);
        when(this.amazonKinesis.describeStream(
            this.iPLoader.getStringProperty("spring.application.name")))
                    .thenReturn(result);
        when(this.validator.validate(EVENT_NAME)).thenReturn(EVENT_NAME);
        Mockito.doNothing().when(this.streamInfoDao).create(streamInformation);
        this.kinesisMessagingService.register(EVENT_NAME);
    }

    /**
     * The method used to test set register event name.
     * 
     * @throws AsyncException
     *             manual exception
     */
    @Test
    public void testRegisterSet() throws AsyncException {

        when(this.topicChache.getRegisterMap()).thenReturn(map);
        when(this.amazonKinesis.describeStream(
            this.iPLoader.getStringProperty("spring.application.name")))
                    .thenReturn(result);
        when(this.validator.validate(EVENT_NAME)).thenReturn(EVENT_NAME);
        Set<String> eventSet = new HashSet<>();
        eventSet.add(EVENT_NAME);
        this.kinesisMessagingService.register(eventSet);

    }

    /**
     * The method used to test publish data on event name.
     * 
     * @throws AsyncException
     *             the manual exception
     */
    @Test
    public void testPublish() throws AsyncException {
        StreamInformation streamInformation = new StreamInformation();
        String data = "Hello test message";
        PutRecordResult value = new PutRecordResult();
        value.setSequenceNumber(
            "49575019184441522000784840241236977236196116668813934610");
        value.setShardId("shardId-000000000001");

        when(this.amazonKinesis.putRecord(
            this.iPLoader.getStringProperty("spring.application.name"),
            ByteBuffer.wrap(data.getBytes()), EVENT_NAME)).thenReturn(value);
        when(this.validator.validate(EVENT_NAME)).thenReturn(EVENT_NAME);
        when(this.streamInfoDao.getStreamInformationByEventName("abc"))
                .thenReturn(streamInformation);
        this.kinesisMessagingService.publish(EVENT_NAME, data);

    }

    /**
     * The method used to test publish data and event name validation fail.
     * 
     * @throws AsyncException
     *             the manual exception
     */
    @Test
    public void testPublishCallbackValidationFail() throws AsyncException {
        String data = "Hello test message";
        PutRecordResult value = new PutRecordResult();
        value.setSequenceNumber(
            "49575019184441522000784840241236977236196116668813934610");
        value.setShardId("shardId-000000000001");

        when(this.amazonKinesis.putRecord(
            this.iPLoader.getStringProperty("stream.name"),
            ByteBuffer.wrap(data.getBytes()), EVENT_NAME)).thenReturn(value);
        when(this.validator.validate(EVENT_NAME)).thenReturn(EVENT_NAME);
        this.kinesisMessagingService.publish(EVENT_NAME, data,
            callBackFunction);

    }

    /**
     * The method used to test publish data on event name with callback.
     * 
     * @throws AsyncException
     *             the manual exception
     */
    @Test
    public void testPublishCallback() throws AsyncException {
        StreamInformation streamInformation = new StreamInformation();
        String data = "Hello test message";
        PutRecordResult value = new PutRecordResult();
        value.setSequenceNumber(
            "49575019184441522000784840241236977236196116668813934610");
        value.setShardId("shardId-000000000001");
        when(this.topicChache.getRegisterMap()).thenReturn(map);

        when(this.amazonKinesis.putRecord(
            this.iPLoader.getStringProperty("stream.name"),
            ByteBuffer.wrap(data.getBytes()), EVENT_NAME)).thenReturn(value);
        when(this.validator.validate(EVENT_NAME)).thenReturn(EVENT_NAME);
        when(this.streamInfoDao.getStreamInformationByEventName("abc"))
                .thenReturn(streamInformation);
        this.kinesisMessagingService.publish(EVENT_NAME, data,
            callBackFunction);

    }

    /**
     * The method used to test subscribe data on event name with callback.
     * 
     * @throws AsyncException
     */
    @Test
    public void testSubscribe() throws AsyncException {
        StreamInformation streamInformation = new StreamInformation();
        Shard shard = new Shard();
        shard.setShardId("shardId-000000000001");
        when(this.topicChache.getRegisterMap()).thenReturn(map);
        when(this.amazonKinesis
                .describeStream(this.iPLoader.getStringProperty("stream.name")))
                        .thenReturn(result);
        when(this.validator.validate(EVENT_NAME)).thenReturn(EVENT_NAME);
        when(this.streamInfoDao.getStreamInformationByEventName("abc"))
                .thenReturn(streamInformation);
        this.kinesisMessagingService.subscribe(EVENT_NAME, callBackFunction);

    }

    /**
     * The method used to test subscribe data with validation fail.
     * 
     * @throws AsyncException
     */
    @Test
    public void testSubscribeValidationFail() throws AsyncException {
        Shard shard = new Shard();
        shard.setShardId("shardId-000000000001");
        when(this.topicChache.getRegisterMap()).thenReturn(map);
        when(this.amazonKinesis
                .describeStream(this.iPLoader.getStringProperty("stream.name")))
                        .thenReturn(result);
        this.kinesisMessagingService.subscribe(EVENT_NAME, callBackFunction);

    }

    /**
     * The method used to test publish data on event name with callback.
     */
    @Test
    public void testSubscribeWithMap() {

        Shard shard = new Shard();
        shard.setShardId("shardId-000000000001");
        when(this.topicChache.getRegisterMap()).thenReturn(map);
        when(this.amazonKinesis
                .describeStream(this.iPLoader.getStringProperty("stream.name")))
                        .thenReturn(result);
        Map<String, Consumer<String>> eventCallBackMap = new HashMap<>();
        eventCallBackMap.put(EVENT_NAME, callBackFunction);
        this.kinesisMessagingService.subscribe(eventCallBackMap);

    }

    /**
     * The method used to test Describe Stream of stream name .
     */
    @Test
    public void testDescribeStream() {
        DescribeStreamResult describeStreamResult = new DescribeStreamResult();
        StreamDescription streamDescription = new StreamDescription();
        streamDescription.setStreamStatus("ACTIVE");
        describeStreamResult.setStreamDescription(streamDescription);
        DescribeStreamRequest describeStreamRequest =
                new DescribeStreamRequest();
        describeStreamRequest.setStreamName(STREAM_NAME+1);
        Mockito.when(this.amazonKinesis.describeStream(describeStreamRequest))
                .thenReturn(describeStreamResult);

        StreamDescription description =
                this.kinesisMessagingService.describeStream(STREAM_NAME+1);
        Assert.assertNotNull(description);
        Assert.assertEquals("ACTIVE", description.getStreamStatus());
    }

    /**
     * The method used to test Describe Stream of stream name when not found .
     */
    @Test
    public void testDescribeStreamNotFound() {
        DescribeStreamResult describeStreamResult = new DescribeStreamResult();
        StreamDescription streamDescription = new StreamDescription();
        streamDescription.setStreamStatus("ACTIVE");
        describeStreamResult.setStreamDescription(null);
        DescribeStreamRequest describeStreamRequest =
                new DescribeStreamRequest();
        describeStreamRequest.setStreamName(STREAM_NAME+2);
        Mockito.when(this.amazonKinesis.describeStream(describeStreamRequest))
                .thenThrow(new ResourceNotFoundException("resource not found"));

        StreamDescription description =
                this.kinesisMessagingService.describeStream(STREAM_NAME+2);
        Assert.assertNotNull(description);
        Assert.assertEquals("NOTEXISTS", description.getStreamStatus());
    }

    /**
     * The method used to test Describe Stream when connection not establish .
     */
    @Test
    public void testDescribeStreamAwsExeception() {
        DescribeStreamResult describeStreamResult = new DescribeStreamResult();
        StreamDescription streamDescription = new StreamDescription();
        streamDescription.setStreamStatus("ACTIVE");
        describeStreamResult.setStreamDescription(null);
        DescribeStreamRequest describeStreamRequest =
                new DescribeStreamRequest();
        describeStreamRequest.setStreamName(STREAM_NAME+3);
        Mockito.when(this.amazonKinesis.describeStream(describeStreamRequest))
                .thenThrow(
                    new AmazonKinesisException("connection not establish"));

        StreamDescription description =
                this.kinesisMessagingService.describeStream(STREAM_NAME+3);
        Assert.assertNotNull(description);
        Assert.assertEquals("VALIDATIONFAIL", description.getStreamStatus());
        
    }

    /**
     * The method used to test create Stream .
     */
    @Test
    public void testCreateStream() {
        DescribeStreamResult describeStreamResult = new DescribeStreamResult();
        StreamDescription streamDescription = new StreamDescription();
        streamDescription.setStreamStatus("ACTIVE");
        describeStreamResult.setStreamDescription(streamDescription);
        CreateStreamResult result = new CreateStreamResult();
        Mockito.when(this.iPLoader.getIntegerProperty("shard.count"))
                .thenReturn(1);
        Mockito.when(this.amazonKinesis.createStream(STREAM_NAME, 1))
                .thenReturn(result);
        Mockito.when(this.amazonKinesis.describeStream(STREAM_NAME))
                .thenReturn(describeStreamResult);
        Mockito.when(this.iPLoader.getIntegerProperty("stream.create.timeout"))
                .thenReturn(100);
        this.kinesisMessagingService.createStream(STREAM_NAME);
    }

    /**
     * The method used to test delete Stream .
     */
    @Test
    public void testDeleteStream() {
        DeleteStreamRequest deleteStreamRequest = new DeleteStreamRequest();
        deleteStreamRequest.setStreamName(STREAM_NAME);
        DeleteStreamResult deleteStreamResult = new DeleteStreamResult();
        Mockito.when(this.amazonKinesis.deleteStream(deleteStreamRequest))
                .thenReturn(deleteStreamResult);
        this.kinesisMessagingService.deleteStream(STREAM_NAME);
    }

    /**
     * The method used to test delete Stream .
     */
    @Test
    public void testUpdateShard() {
        UpdateShardCountResult shardCountResult = new UpdateShardCountResult();
        UpdateShardCountRequest countRequest = new UpdateShardCountRequest();
        countRequest.setStreamName(STREAM_NAME);
        countRequest.setTargetShardCount(4);
        Mockito.when(this.amazonKinesis.updateShardCount(countRequest))
                .thenReturn(shardCountResult);
        this.kinesisMessagingService.updateShards(STREAM_NAME, 4);
    }

}
