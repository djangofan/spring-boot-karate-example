package com.test.demo.controllers;

import com.github.javafaker.Faker;
import com.google.common.util.concurrent.Uninterruptibles;
import com.test.demo.WmqTestCounters;
import io.vavr.collection.Iterator;
import io.vavr.concurrent.Future;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Slf4j
public class WmqTestController {
    private final AtomicInteger sleepInterval = new AtomicInteger(666);
    private final AtomicInteger msgCount = new AtomicInteger(1000);
    private final Random random = new Random();

    @Autowired
    private JmsTemplate myJmsTemplate;

    @Value(value = "${ibm.mq.destination}")
    private String queueName;


    @PostMapping("/interval/{timout}")
    @SneakyThrows
    public void setInter(@PathVariable int timout) {
        sleepInterval.set(timout);
    }

    @GetMapping("/interval")
    @SneakyThrows
    public int setInterval() {
        return sleepInterval.get();
    }

    @PostMapping("/msgCount/{count}")
    @SneakyThrows
    public void setMsgCount(@PathVariable int count) {
        msgCount.set(count);
    }

    @GetMapping("/msgCount")
    @SneakyThrows
    public int msgCount() {
        return msgCount.get();
    }

    @GetMapping("/send")
    @SneakyThrows
    public String send() {
        return "OK: " + sendMessage(WmqTestCounters.sent.get(), getPayload());
    }

    @GetMapping("/start/test")
    @SneakyThrows
    public void startTest() {
        Future.of(() -> {
            Iterator.range(0, msgCount.get()).forEach(x -> {
                sleep(sleepInterval.get());
                sendMessage(x, getPayload());
            });

            return "OK";
        });
    }

    @GetMapping("/start/test-random/{low}/{high}")
    @SneakyThrows
    public void startTest(@PathVariable int low, @PathVariable int high) {
        Future.of(() -> {
            Iterator.range(0, msgCount.get()).forEach(x -> {
                randomSleep(low, high);
                sendMessage(x, getPayload());
            });

            return "OK";
        });
    }

    @GetMapping("/msg-counters")
    public Map<String, Integer> getTestCounters() {
        return WmqTestCounters.getCounters().toJavaMap();
    }

    private static void sleep(long interval) {
        Uninterruptibles.sleepUninterruptibly(interval, TimeUnit.MILLISECONDS);
        log.info("paused for {} ms", interval);
    }

    private static String getPayload() {
        return new Faker().lorem().paragraph(5);
    }

    public void randomSleep(int min, int max) {
        int interval = this.random.nextInt(max - min) + min;
        sleep(interval);
    }

    private int sendMessage(int x, String msg) {
        boolean go = true;

        while (go) {
            try {
                StopWatch started = StopWatch.createStarted();
                myJmsTemplate.convertAndSend(queueName, x + ": " + msg);
                started.stop();
                log.info("message {} sent in {} ms", x, started.getTime());
                go = false;
            } catch (JmsException e) {
                log.error("send failed. will try again. jsm exception: {}", e.toString());
                sleep(30_000);
            }
        }

        return WmqTestCounters.sent.incrementAndGet();
    }
}
