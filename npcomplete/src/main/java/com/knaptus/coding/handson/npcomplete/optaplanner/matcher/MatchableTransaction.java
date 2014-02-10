package com.knaptus.coding.handson.npcomplete.optaplanner.matcher;

import com.knaptus.coding.handson.npcomplete.optaplanner.matcher.comparator.MatchDifficultyComparator;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.math.BigDecimal;

/**
 * Allows a transaction to be marked as matched.
 *
 * @author Aditya Bhardwaj
 */
@PlanningEntity(difficultyComparatorClass = MatchDifficultyComparator.class)
public class MatchableTransaction {

    private Boolean matched = null;

    private Transaction transaction;

    public MatchableTransaction() {
    }

    public MatchableTransaction(long cost, long quantity) {
        this.transaction = new Transaction(cost, quantity);
    }

    public MatchableTransaction(double cost, long quantity) {
        this.transaction = new Transaction(cost, quantity);
    }

    public MatchableTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @PlanningVariable(valueRangeProviderRefs = {"matched"}, nullable = true)
    public Boolean getMatched() {
        return matched;
    }

    public void setMatched(Boolean match) {
        this.matched = match;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public BigDecimal getCost() {
        return transaction.getCost();
    }

    public long getQuantity() {
        return transaction.getQuantity();
    }

    public BigDecimal getPrice() {
        return transaction.getPrice();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
