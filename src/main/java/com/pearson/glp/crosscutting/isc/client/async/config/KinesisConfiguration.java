
package com.pearson.glp.crosscutting.isc.client.async.config;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.pearson.glp.crosscutting.isc.client.async.service.MessagingService;
import com.pearson.glp.crosscutting.isc.client.async.service.impl.KinesisMessagingService;

/**
 * The KinesisConfiguration class.
 * 
 * @author Md Sakib
 *
 */
@Component
@ComponentScan(
        value = "com.pearson.glp.crosscutting.isc.client.async.service.impl")
@Profile("kinesis")
public class KinesisConfiguration {
    /**
     * Object of Logger class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(KinesisConfiguration.class);
    private static final String ALGO = "AES";

    /**
     * Instantiates a new KinesisConfiguration.
     */
    public KinesisConfiguration() {
        super();
    }

    /**
     * The object of IscPropertiesLoader.
     */
    @Autowired
    private IscPropertiesLoader iscPropLoader;

    /**
     * The method create object of AmazonKinesis.
     * 
     * @return object of AmazonKinesis
     */
    @Bean
    public AmazonKinesis getAmazonKinesis() {
        
       /* AWSSecurityTokenServiceClient sts_client = new AWSSecurityTokenServiceClient();
        sts_client.setEndpoint("sts-endpoint.amazonaws.com");
        GetSessionTokenRequest session_token_request = new GetSessionTokenRequest();
        session_token_request.setDurationSeconds(7200);
        GetSessionTokenResult session_token_result =
                sts_client.getSessionToken(session_token_request);
        Credentials session_creds = session_token_result.getCredentials();
        BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
            session_creds.getAccessKeyId(),
            session_creds.getSecretAccessKey(),
            session_creds.getSessionToken());
        return AmazonKinesisClientBuilder.standard()
                .withRegion(this.iscPropLoader.getStringProperty("aws.regions"))
                .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
                .build();*/
        
        
        
      /* AWSCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        return AmazonKinesisClientBuilder.standard()
                .withRegion(this.iscPropLoader.getStringProperty("aws.regions")).withCredentials(credentialsProvider)
                .build();
*/
        
       return AmazonKinesisClientBuilder.standard()
         .withRegion(this.iscPropLoader.getStringProperty("aws.regions"))
         .withCredentials( new
         AWSStaticCredentialsProvider(this.getAWSCredentials())) .build();
        
    }

    /**
     * The instance create.
     * 
     * @return object of MessagingService
     */
    @Bean
    public MessagingService getIscAsyncClient() {

        return new KinesisMessagingService();

    }

    /**
     * The method create object of AWSCredentials.
     * 
     * @return object of AWSCredentials
     */
    private AWSCredentials getAWSCredentials() {

        AWSCredentials credentials = null;
        try {
            credentials = new AWSCredentials() {

                @Override
                public String getAWSSecretKey() {
                    return decrypt(
                        iscPropLoader.getStringProperty("aws.secret.key"));
                }

                @Override
                public String getAWSAccessKeyId() {

                    return decrypt(
                        iscPropLoader.getStringProperty("aws.access.key"));
                }
            };
        } catch (AmazonClientException exception) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the database properties "
                            + "file. Please make sure that correct credentials"
                            + " are available in database.properties file in "
                            + "location (~/config/{enviornment}/), and is in "
                            + "valid format.",
                    exception);
        }

        return credentials;
    }

    private String decrypt(String encryptedData) {
        try {
            Key key = new SecretKeySpec(this.iscPropLoader
                    .getStringProperty("secret.key").getBytes(), ALGO);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = Base64.decodeBase64(encryptedData);
            byte[] decValue = cipher.doFinal(decordedValue);
            return new String(decValue);
        } catch (Exception e) {
            LOGGER.error("Exeption in decrypt data {}", e);
            return null;
        }
    }

}
