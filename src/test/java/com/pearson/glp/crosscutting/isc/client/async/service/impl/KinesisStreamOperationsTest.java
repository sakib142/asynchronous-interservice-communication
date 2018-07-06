package com.pearson.glp.crosscutting.isc.client.async.service.impl;

import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.GetRecordsRequest;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest;
import com.amazonaws.services.kinesis.model.GetShardIteratorResult;
import com.amazonaws.services.kinesis.model.Record;
import com.pearson.glp.crosscutting.isc.client.async.config.IscPropertiesLoader;

/**
 * The KinesisStreamOperationsTest class.
 * 
 * @author Md Sakib
 *
 */
public class KinesisStreamOperationsTest {

    /**
     * KinesisStreamOperationsTest instantiate.
     */
    public KinesisStreamOperationsTest() {
        super();
    }

    private final static String EVENT_NAME = "abc";

    private final static String SHARD_ID = "shardId-000000000001";

    private final static String SHARD_TYPE = "LATEST";

    private final static String STREAM_NAME = "isc_stream_demo";

    /**
     * The mock object of IscPropertiesLoader.
     */
    @Mock
    private IscPropertiesLoader iPLoader;

    /**
     * The mock object of AmazonKinesis.
     */
    @Mock
    private AmazonKinesis amazonKinesis;

    /**
     * Inject mock object of KinesisMessagingService.
     */
    @InjectMocks
    private KinesisStreamOperations kinesisStreamOperations;

    private Consumer<String> callBackFunction = x -> {
        System.out.println("Consume message : "
                + x);
    };

    /**
     * Setup and mocking required the object.
     */
    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(this.iPLoader.getIntegerProperty("stream.sleep.time"))
                .thenReturn(1000);

    }

    @BeforeMethod
    public void beforeMethod() {

    }

    /**
     * The method used to test get record from stream.
     */
    @Test
    public void testGetRecordsFromStream() {
        GetShardIteratorRequest sItrRqst = new GetShardIteratorRequest();
        sItrRqst.setStreamName(STREAM_NAME);
        sItrRqst.setShardId(SHARD_ID);
        sItrRqst.setShardIteratorType(SHARD_TYPE);
        GetShardIteratorResult value = new GetShardIteratorResult();
        value.setShardIterator(SHARD_TYPE);
       
        when(this.amazonKinesis.getShardIterator(sItrRqst)).thenReturn(value);
        
        GetRecordsRequest gRcdsRqst = new GetRecordsRequest();
        gRcdsRqst.setShardIterator(SHARD_TYPE);
        gRcdsRqst.setLimit(1000);
        GetRecordsResult gRsResult = new GetRecordsResult();
        Collection<Record> records = new HashSet<>();
        Record record = new Record();
        record.setPartitionKey("abc");
        record.setData(ByteBuffer.wrap("hello".getBytes()));
        record.setSequenceNumber("12344");
        records.add(record);
        gRsResult.setRecords(records);
        when(this.amazonKinesis.getRecords(gRcdsRqst)).thenReturn(gRsResult);
        this.kinesisStreamOperations.getRecordsFromStream(SHARD_ID, EVENT_NAME,
            SHARD_TYPE, STREAM_NAME, callBackFunction);
    }

}
