package com.test.demo;

import java.util.concurrent.atomic.AtomicInteger;

public class WmqTestCounters {

    public static AtomicInteger sent = new AtomicInteger(0);
    public static AtomicInteger received = new AtomicInteger(0);

    public static io.vavr.collection.HashMap<String, Integer> getCounters() {
        return io.vavr.collection.HashMap.of("sent", sent.get(), "received", received.get());
    }
    
}
