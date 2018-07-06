package com.pearson.glp.crosscutting.isc.client.async.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

/**
 * The IscPropertiesLoader class.
 * 
 *<p>Read the value from properties.
 * 
 * @author Md Sakib
 *
 */
@Configuration
@ComponentScan(basePackages = { "com.pearson" })
@PropertySource(value = {
        "file:${config.home}/${env}/ProducerConfiguration.properties",
        "file:${config.home}/${env}/ConsumerConfiguration.properties",
        "file:${config.home}/${env}/ZookeeperConfiguration.properties",
        "file:${config.home}/${env}/KinesisConfiguration.properties"})
public class IscPropertiesLoader {
	 /**
     * IscPropertiesLoader instantiate.
     */
    public IscPropertiesLoader() {
        super();
    }
    /**
     * The instance of IscPropertiesLoader class.
     */
    @Autowired
    private Environment env;

    /**
     * The method create the object.
     * 
     *<p>of PropertySourcesPlaceholderConfigurer.
     *  
     * @return object of PropertySourcesPlaceholderConfigurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer
           propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     * Get the Property Value as Integer.
     * 
     * @param name
     *            property name
     * @return the string
     */
    public Integer getIntegerProperty(String name) {
        if (this.getString(name) != null) {
            return Integer.valueOf(this.getString(name));
        } else {
            return null;
        }
    }

    /**
     * Get the Property Value as Boolean.
     * 
     * @param name
     *            property name
     * @return the boolean
     */
    public Boolean getBooleanProperty(String name) {
        if (this.getString(name) != null) {
            return Boolean.valueOf(this.getString(name));
        } else {
            return Boolean.FALSE;
        }

    }

    /**
     * Get the property value as String.
     * 
     * @param name
     *            property name
     * @return the string
     */
    public String getStringProperty(String name) {
        return this.getString(name);
    }

    /**
     * Gets the string.
     *
     * @param name
     *            the name
     * @return the string
     */
    private String getString(String name) {
        return this.env.getProperty(name);
    }

}
