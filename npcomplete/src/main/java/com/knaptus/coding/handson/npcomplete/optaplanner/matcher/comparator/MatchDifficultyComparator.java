package com.knaptus.coding.handson.npcomplete.optaplanner.matcher.comparator;

import com.google.common.collect.ComparisonChain;
import com.knaptus.coding.handson.npcomplete.optaplanner.matcher.MatchableTransaction;

import java.util.Comparator;

/**
 * Returns bigger transaction in quantity and price as lower difficulty.
 *
 * @author Aditya Bhardwaj
 */
public class MatchDifficultyComparator implements Comparator<MatchableTransaction> {

    @Override
    public int compare(MatchableTransaction o1, MatchableTransaction o2) {
        return ComparisonChain.start()
        .compare(o1.getQuantity(), o2.getQuantity())
        .compare(o1.getPrice(), o2.getPrice()).result() * -1;
    }
}
