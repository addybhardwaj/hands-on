package com.intelladept.poc.esper.events;

/**
 * @author Aditya Bhardwaj
 */
public class ReceivedEvent extends TxnEventBase {

    public ReceivedEvent(String transactionId, long timestamp) {
        super(transactionId, timestamp);
    }
}
