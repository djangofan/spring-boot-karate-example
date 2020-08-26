package com.test.demo.config;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.backoff.FixedBackOff;

import javax.jms.JMSException;

import static org.apache.commons.lang3.StringUtils.*;

@Configuration
@EnableJms
@Getter
@Setter
@Slf4j
public class MQConfig {

    @Value("${ibm.mq.queueManager}")
    private String queueManager;

    @Value("${ibm.mq.channel}")
    private String channel;

    @Value("${ibm.mq.hostName}")
    private String hostName;

    @Value("${ibm.mq.port}")
    private int port;

    @Value("${ibm.mq.username}")
    private String username;

    @Value("${ibm.mq.password}")
    private String password;

    @Value("${ibm.mq.timeout}")
    private long timeout;

    @Value("${ibm.mq.maxAttempts}")
    private int maxAttempts;

    @Value("${ibm.mq.cipher:}")
    private String cipher;

    @Value("${ibm.mq.useIBMciphermappings:}")
    private String useIBMciphermappings;

    @Value("${ibm.mq.mqcspauthenable:false}")
    private boolean isMqcspAuthEnabled;

    @Value("${spring.application.name}")
    private String applicationName;


    @Bean
    public MQQueueConnectionFactory mqQueueConnectionFactory() throws JMSException {
        MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
        mqQueueConnectionFactory.setHostName(hostName);

        mqQueueConnectionFactory.setQueueManager(queueManager);
        mqQueueConnectionFactory.setPort(port);
        mqQueueConnectionFactory.setChannel(channel);
        mqQueueConnectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
        mqQueueConnectionFactory.setAppName(applicationName);

        mqQueueConnectionFactory.setStringProperty(WMQConstants.USERID, username);
        mqQueueConnectionFactory.setStringProperty(WMQConstants.PASSWORD, password);

        mqQueueConnectionFactory.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, isMqcspAuthEnabled);

        mqQueueConnectionFactory.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SPEC, defaultString(cipher, null));

        if (isNoneBlank(useIBMciphermappings))
            System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", useIBMciphermappings);

        return mqQueueConnectionFactory;
    }

    @Bean(name = "myJmsTemplate")
    public JmsTemplate myJmsTemplate(MQQueueConnectionFactory mqQueueConnectionFactory) {
        return new JmsTemplate(mqQueueConnectionFactory);
    }


    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory(MQQueueConnectionFactory mqQueueConnectionFactory) {

        DefaultJmsListenerContainerFactory jmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();
        jmsListenerContainerFactory.setConnectionFactory(mqQueueConnectionFactory);
        jmsListenerContainerFactory.setPubSubDomain(false);

        jmsListenerContainerFactory.setBackOff(new FixedBackOff(timeout, maxAttempts));
        jmsListenerContainerFactory.setErrorHandler(error -> log.error("Error while creating JMS Listener Container {}", error.toString()));

        return jmsListenerContainerFactory;
    }

}
