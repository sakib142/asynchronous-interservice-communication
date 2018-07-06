package com.pearson.glp.crosscutting.isc.client.async.model;

/**
 * The EventMessage class.
 * 
 * @author Md Sakib
 *
 */
public class EventMessage {
    /**
     * EventMessage instantiate.
     */
    public EventMessage() {
        super();
    }

    /**
     * The instance variable id.
     */
    private String id;

    /**
     * The instance variable timestamp.
     */
    private String timestamp;

    /**
     * The instance variable eventName.
     */
    private String eventName;

    /**
     * The instance variable payload.
     */
    private String payload;

    /**
     * Get id.
     * 
     * @return string id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Set id.
     * 
     * @param id
     *            value of id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get timestamp.
     * 
     * @return string time
     */
    public String getTimestamp() {
        return this.timestamp;
    }

    /**
     * Set time.
     * 
     * @param timestamp
     *            value in time
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get event name.
     * 
     * @return string get event name
     */
    public String getEventName() {
        return this.eventName;
    }

    /**
     * Set event name.
     * 
     * @param eventName
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Get payload.
     * 
     * @return string payload value
     */
    public String getPayload() {
        return this.payload;
    }

    /**
     * Set payload.
     * 
     * @param payload
     *            value of payload
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }

}
