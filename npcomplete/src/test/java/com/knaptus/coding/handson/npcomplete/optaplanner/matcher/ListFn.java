package com.knaptus.coding.handson.npcomplete.optaplanner.matcher;

import java.math.BigDecimal;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public final class ListFn {

    public static <R, T> R agg(Iterable<T> iterable, AggregateFn<R, T> fn) {
        return agg(iterable, fn, null);
    }

    public static <R, T> R agg(Iterable<T> iterable, AggregateFn<R, T> fn, R start) {

        for (T element : iterable) {
            start = fn.aggregate(start, element);
        }
        return start;
    }

    public static interface AggregateFn<R, T> {
        R aggregate(R start, T element);
    }

    public static final AggregateFn<Long, Transaction> QUANTITY_FN = new QuantityAggFn();
    public static final AggregateFn<BigDecimal, Transaction> COST_FN = new CostAggFn();

    private static final class QuantityAggFn implements AggregateFn<Long, Transaction> {

        @Override
        public Long aggregate(Long start, Transaction element) {
            return (start==null? 0: start) + element.getQuantity();
        }
    }

    private static final class CostAggFn implements AggregateFn<BigDecimal, Transaction> {

        @Override
        public BigDecimal aggregate(BigDecimal start, Transaction element) {
            return (start==null? BigDecimal.ZERO: start).add(element.getCost());
        }
    }
}
