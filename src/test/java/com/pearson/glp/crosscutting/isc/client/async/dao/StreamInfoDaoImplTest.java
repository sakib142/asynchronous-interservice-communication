package com.pearson.glp.crosscutting.isc.client.async.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
//import static org.mockito.Mockito.when;
import com.pearson.glp.crosscutting.isc.client.async.model.StreamInformation;

/**
 * The StreamInfoDaoImplTest class.
 * 
 * 
 *
 */
public class StreamInfoDaoImplTest {

    /**
     * The default constructor.
     */
    public StreamInfoDaoImplTest() {
        super();
    }

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private StreamInfoDaoImpl streamInfoDaoImpl;

    private final static String EVENT_NAME = "abc";

    private StreamInformation streamInfo;

    /**
     * Setup and mocking required the object.
     */
    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Setup before execute method.
     */
    @BeforeMethod
    public void beforeMethod() {
        this.streamInfo = new StreamInformation();
        this.streamInfo.setEventName(EVENT_NAME);
        this.streamInfo.setStreamName("demo_stream");
    }

    /**
     * The method used to test create stream name.
     * 
     */
    @Test
    public void testCreate() {
        Mockito.doNothing().when(this.entityManager).persist(this.streamInfo);
        this.streamInfoDaoImpl.create(this.streamInfo);
    }

    /**
     * The method used to test update stream shards.
     * 
     */
    @Test
    public void testUpdate() {
        Mockito.when(this.entityManager.merge(this.streamInfo)).thenReturn(this.streamInfo);
        StreamInformation information = this.streamInfoDaoImpl.update(this.streamInfo);
        Assert.assertNotNull(information);
    }

    /**
     * The method used to test getStreamInformationById.
     * 
     */
    @Test
    public void testGetStreamInformationById() {
        long id = 1;
        Mockito.when(this.entityManager.find(StreamInformation.class, id))
                .thenReturn(this.streamInfo);
        StreamInformation information =
                this.streamInfoDaoImpl.getStreamInformationById(1);
        Assert.assertNotNull(information);
    }

    /**
     * The method used to test getStreamInformationById.
     * 
     */
    @Test
    public void testGetStreamInformationByEventName() {
        String sql = "FROM StreamInformation WHERE eventName=:e";
        Query query = Mockito.mock(Query.class);
        Mockito.when(this.entityManager.createQuery(sql)).thenReturn(query);
        StreamInformation information =
                this.streamInfoDaoImpl.getStreamInformationByEventName(EVENT_NAME);
        Assert.assertNull(information);
    }
    
    @Test
    public void testDelete() {
        long id = 1;
        Mockito.when(this.entityManager.find(StreamInformation.class, id))
                .thenReturn(this.streamInfo);
        StreamInformation information =
                this.streamInfoDaoImpl.getStreamInformationById(1);
        Mockito.doNothing().when( this.entityManager).remove(information);
        this.streamInfoDaoImpl.delete(id);
    }

}
