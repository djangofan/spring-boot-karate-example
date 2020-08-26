package com.test.demo.listeners;

import com.test.demo.WmqTestCounters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WmbTestListener {

    @JmsListener(destination = "${ibm.mq.destination}", containerFactory = "jmsListenerContainerFactory")
    public void receiveMessage(String msg) {
        log.info("received msg: {}", msg);

        WmqTestCounters.received.incrementAndGet();
    }
}
