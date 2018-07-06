package com.pearson.glp.crosscutting.isc.client.async.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pearson.glp.crosscutting.isc.client.async.model.StreamInformation;

@Repository
@Transactional
public class StreamInfoDaoImpl implements StreamInfoDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Object of Logger class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(StreamInfoDaoImpl.class);
    
    @Override
    public void create(StreamInformation streamInfo) {
        this.entityManager.persist(streamInfo);
    }

    @Override
    public StreamInformation update(StreamInformation streamInfo) {
       return this.entityManager.merge(streamInfo);
    }

    @Override
    public StreamInformation getStreamInformationById(long id) {
        return this.entityManager.find(StreamInformation.class, id);
    }

    @Override
    public StreamInformation getStreamInformationByEventName(String eventName) {
        StreamInformation streamInformation=null;
        String sql = "FROM StreamInformation WHERE eventName=:e";
        Query query = this.entityManager.createQuery(sql);
        try{
        query.setParameter("e", eventName);
        streamInformation=(StreamInformation) query.getSingleResult();
        }catch (NoResultException e) {
            LOGGER.warn("EventName {} : {} ",eventName,e.getMessage());
        }
        catch (Exception e) {
            LOGGER.warn("Exception while reading data from stream {}",e);
        }
        return  streamInformation;
    }

    @Override
    public void delete(long id) {
        StreamInformation streamInfo = this.getStreamInformationById(id);
        if (streamInfo != null) {
            this.entityManager.remove(streamInfo);
        }
    }

}
