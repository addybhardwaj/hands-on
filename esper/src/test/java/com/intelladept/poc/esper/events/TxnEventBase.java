package com.intelladept.poc.esper.events;

/**
 * @author Aditya Bhardwaj
 */
public class TxnEventBase {
    private String transactionId;
    private long timestamp;

    public TxnEventBase(String transactionId, long timestamp) {
        this.transactionId = transactionId;
        this.timestamp = timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "TxnEventBase{" +
                "transactionId='" + transactionId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
