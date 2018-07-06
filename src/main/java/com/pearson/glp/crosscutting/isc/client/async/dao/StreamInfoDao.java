package com.pearson.glp.crosscutting.isc.client.async.dao;

import com.pearson.glp.crosscutting.isc.client.async.model.StreamInformation;

public interface StreamInfoDao {

    public void create(StreamInformation streamInfo);

    public StreamInformation update(StreamInformation streamInfo);

    public StreamInformation getStreamInformationById(long id);
    
    public StreamInformation getStreamInformationByEventName(String eventName);
    
    public void delete(long id);
    
}
