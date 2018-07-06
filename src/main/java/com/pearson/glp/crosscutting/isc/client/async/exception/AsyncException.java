package com.pearson.glp.crosscutting.isc.client.async.exception;

/**
 * The AsyncException class.
 * 
 * @author Md Sakib
 *
 */
public class AsyncException extends Exception {

    /**
     * A unique serial version identifier.
     * 
     * @see Serializable#serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * The method set exception message.
     * 
     * @param message
     *            description of message
     */
    public AsyncException(String message) {
        super(message);

    }

}
