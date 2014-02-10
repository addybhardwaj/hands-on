package com.knaptus.coding.handson.npcomplete.optaplanner.matcher;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.value.ValueRangeProvider;
import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.impl.solution.Solution;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
@PlanningSolution
public class MatchingProblem<T extends AbstractScore> implements Solution<T> {

    private Transaction netTransaction;

    private List<MatchableTransaction> transactions;

    private T score;

    @PlanningEntityCollectionProperty
    public List<MatchableTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<MatchableTransaction> transactions) {
        this.transactions = transactions;
    }

    @ValueRangeProvider(id = "matched")
    public List<Boolean> getMatched() {
        return Arrays.asList(Boolean.TRUE);
    }

    public Transaction getNetTransaction() {
        return netTransaction;
    }

    public void setNetTransaction(Transaction netTransaction) {
        this.netTransaction = netTransaction;
    }

    @Override
    public T getScore() {
        return score;
    }

    @Override
    public void setScore(T score) {
        this.score = score;
    }

    @Override
    public Collection<?> getProblemFacts() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
