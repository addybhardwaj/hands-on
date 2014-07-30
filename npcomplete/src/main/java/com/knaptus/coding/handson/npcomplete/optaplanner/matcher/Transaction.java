package com.knaptus.coding.handson.npcomplete.optaplanner.matcher;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * Basic transaction.
 *
 * @author Aditya Bhardwaj
 */
public class Transaction {

    private BigDecimal cost;
    private BigDecimal price;
    private long quantity;

    public Transaction() {}

    public Transaction(long cost, long quantity) {
        this((double)cost,quantity);
    }

    public Transaction(double cost, long quantity) {
        this.cost = BigDecimal.valueOf(cost);
        this.quantity = quantity;
        this.price = this.cost.divide(BigDecimal.valueOf(quantity));
    }

    public Transaction(BigDecimal cost, long quantity) {
        this.cost = cost;
        this.quantity = quantity;
        this.price = this.cost.divide(BigDecimal.valueOf(quantity));
    }

    public Transaction(long quantity, BigDecimal price) {
        this.price = price;
        this.cost = price.multiply(BigDecimal.valueOf(quantity));
        this.quantity = quantity;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public long getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
