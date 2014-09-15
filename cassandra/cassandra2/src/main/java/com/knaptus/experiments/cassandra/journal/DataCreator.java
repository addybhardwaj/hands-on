package com.knaptus.experiments.cassandra.journal;

import java.util.concurrent.atomic.AtomicLong;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class DataCreator {

    AtomicLong counter = new AtomicLong(0);

    String data = "<xml>";

    public DataCreator() {
        for(int i=0; i<1000; i++) {
            data += "<xml>" + i + "</xml>";
        }
    }

    public String createPayload() {
        return counter.getAndIncrement() + " >> payload >> " + data;
    }
}
