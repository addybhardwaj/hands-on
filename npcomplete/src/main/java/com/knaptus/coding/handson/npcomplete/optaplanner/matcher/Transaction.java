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
    private long quantity;
    private BigDecimal price;

    public Transaction() {}

    public Transaction(long cost, long quantity) {
        this.cost = new BigDecimal(cost);
        this.quantity = quantity;
    }

    public Transaction(double cost, long quantity) {
        this.cost = new BigDecimal(cost);
        this.quantity = quantity;
    }

    public BigDecimal getCost() {
        return cost;
    }

//    public void setCost(BigDecimal cost) {
//        this.cost = cost;
//    }

    public long getQuantity() {
        return quantity;
    }

//    public void setQuantity(long quantity) {
//        this.quantity = quantity;
//    }

    public BigDecimal getPrice() {
        if (price == null) {
            price = cost.divide(new BigDecimal(quantity));
        }
        return price;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
