package com.intelladept.poc.esper.events;

/**
 * @author Aditya Bhardwaj
 */
public class SentEvent extends TxnEventBase {

    public SentEvent(String transactionId, long timestamp) {
        super(transactionId, timestamp);
    }
}
