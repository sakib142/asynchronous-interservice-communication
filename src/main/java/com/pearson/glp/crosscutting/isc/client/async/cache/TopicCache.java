package com.pearson.glp.crosscutting.isc.client.async.cache;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The TopicCache class.
 * 
 *<p>Which maintain the cache of topics in map.
 *
 * @author Md Sakib
 *
 */
@Component
public class TopicCache {

    /**
     * TopicCache initiating.
     */
    public TopicCache() {
        super();
    }

    /**
     * Object of Logger class.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(TopicCache.class);
    /**
     * The map of topics.
     */
    private Map<String, String> registerMap = new HashMap<>();

    /**
     * Gets the registerMap.
     * 
     * @return registerMap The list of topics.
     */
    public Map<String, String> getRegisterMap() {
        logger.info("RegisterMap is {}", this.registerMap);
        return this.registerMap;
    }

}
