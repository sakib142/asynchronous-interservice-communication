package com.pearson.glp.crosscutting.isc.client.async.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.amazonaws.services.kinesis.model.StreamDescription;
import com.pearson.glp.crosscutting.isc.client.async.service.impl.KinesisMessagingService;

/**
 * The {@link OnLoadStartUp} Class load on start up of application.
 * 
 * @author Md Sakib
 *
 */
@Profile("kinesis")
@Component
public class OnLoadStartUp implements ApplicationListener<ApplicationEvent> {

    private Boolean serviceFlag = false;

    /**
     * Object of Logger class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OnLoadStartUp.class);

    @Autowired
    private KinesisMessagingService kinesisMessagingService;

    @Autowired
    private IscPropertiesLoader iscPropertiesLoader;

    // @Value("${spring.application.name:dead_stream}")
    // private String streamName;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (!this.serviceFlag) {
            String streamName = this.iscPropertiesLoader
                    .getStringProperty("spring.application.name");
            if (streamName != null) {

                StreamDescription streamDescription =
                        this.kinesisMessagingService.describeStream(streamName);
                LOGGER.info("stream description : {}    status : {} ",
                    streamName, streamDescription.getStreamStatus());
                String status = streamDescription.getStreamStatus();

                if (status.equalsIgnoreCase("NOTEXISTS")) {
                    this.kinesisMessagingService.createStream(streamName);

                }
            } else {
                LOGGER.warn("The property name spring.application.name "
                        + "not found in property file");
            }
            this.serviceFlag = true;
        }

    }

}
