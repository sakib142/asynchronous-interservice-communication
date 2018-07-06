package com.pearson.glp.crosscutting.isc.client.async.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pearson.glp.crosscutting.isc.client.async.exception.AsyncException;

/**
 * The Validator class.
 * 
 * @author Md Sakib
 *
 */
@Component
public class Validator {
    /**
     * Validator instantiate.
     */
    public Validator() {
        super();
    }

    /**
     * Object of Logger class.
     */
    public static final Logger LOGGER =
            LoggerFactory.getLogger(Validator.class);
    
    /**
     * The allowed character in topic.
     */
    private static final String ALLOWED_PATTERN = "^[a-zA-Z0-9_-]*$";

    /**
     * The method used to validate event name.
     * 
     * @param eventName
     *            name of event
     * @return string event name
     * @throws AsyncException
     *             manual exception
     */
    public String validate(String eventName) throws AsyncException {
        LOGGER.debug("In validate()");
        String validatedEventName = eventName;
        validatedEventName =
                this.formatStringHavingWhiteSpace(validatedEventName);
        if (this.hasNoSpecialCharacters(validatedEventName)) {
            return validatedEventName;
        } else {
            throw new AsyncException(
                    "Event name contains special characters. Only "
                            + ALLOWED_PATTERN + " is allowed");
        }

    }
    
    /**
     * The method remove the whitespace from string.
     * 
     * @param eventName name of event
     * @return the string 
     * @throws AsyncException manual exception
     */
    private String formatStringHavingWhiteSpace(String eventName)
            throws AsyncException {
        LOGGER.debug("In formatStringHavingWhiteSpace()");

        if (eventName == null
                || eventName.trim().isEmpty()) {
            throw new AsyncException("Event name cannot be null or blank");
        }
        return eventName.replaceAll("^\\s+", "").replaceAll("\\s+$", "")
                .replaceAll("\\s+", "_");
    }

    /**
     * The method check special characters in string.
     * 
     * @param eventName name of event
     * @return boolean value
     */
    private boolean hasNoSpecialCharacters(String eventName) {
        LOGGER.debug("In hasNoSpecialCharacters()");

        String validatedEventName = eventName;

        Pattern pattern = Pattern.compile(ALLOWED_PATTERN);
        Matcher matcher = pattern.matcher(validatedEventName);

        return matcher.matches();

    }

}
