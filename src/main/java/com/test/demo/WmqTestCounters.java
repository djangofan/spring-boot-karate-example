package com.standard.demo;

import io.vavr.collection.HashMap;

import java.util.concurrent.atomic.AtomicInteger;

public class WmqTestCounters {
    public static AtomicInteger sent = new AtomicInteger(0);
    public static AtomicInteger received = new AtomicInteger(0);

    public static HashMap<String, Integer> getCounters() {
        return HashMap.of("sent", sent.get(), "received", received.get());
    }
}
