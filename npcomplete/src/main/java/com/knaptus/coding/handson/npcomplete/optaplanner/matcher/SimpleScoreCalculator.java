package com.knaptus.coding.handson.npcomplete.optaplanner.matcher;

import org.apache.commons.lang3.BooleanUtils;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class SimpleScoreCalculator implements org.optaplanner.core.impl.score.director.simple.SimpleScoreCalculator<MatchingProblem<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleScoreCalculator.class);

    private int count = 0;

    @Override
    public Score calculateScore(MatchingProblem<?> solution) {
        count++;
//        LOGGER.info("Count was {} ", count);
        BigDecimal totalCost = BigDecimal.ZERO;
        long totalQuantity = 0;
        for (MatchableTransaction matchedTransaction : solution.getTransactions()) {
            if (BooleanUtils.isTrue(matchedTransaction.getMatched())) {
                totalCost = totalCost.add(matchedTransaction.getCost());
                totalQuantity += matchedTransaction.getQuantity();
            }
        }

        Transaction netTransaction = solution.getNetTransaction();

        long score = -1 * netTransaction.getCost().subtract(totalCost).abs().longValue();
        score -= Math.abs(solution.getNetTransaction().getQuantity() - totalQuantity);
        return SimpleLongScore.valueOf(score);
    }
}
