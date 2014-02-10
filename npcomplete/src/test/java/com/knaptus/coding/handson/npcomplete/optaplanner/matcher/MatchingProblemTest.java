package com.knaptus.coding.handson.npcomplete.optaplanner.matcher;

import com.knaptus.coding.handson.npcomplete.optaplanner.PlannerRunner;
import com.knaptus.coding.handson.npcomplete.optaplanner.UnsolvedProblemBuilder;
import org.junit.Before;
import org.junit.Test;
import org.optaplanner.core.impl.solution.Solution;

import java.util.ArrayList;

/**
 * Tests.
 *
 * @author Aditya Bhardwaj
 */
public class MatchingProblemTest {

    private PlannerRunner plannerRunner;

    @Before
    public void before() {
        plannerRunner = new PlannerRunner();
    }

    @Test
    public void simpleTest() throws Exception {
        Solution<?> solution = plannerRunner.run("/simpleMatcherSolverConfig.xml", new UnsolvedProblemBuilder() {
            @Override
            public Solution buildProblem() {
                MatchingProblem<?> easyMatching = new MatchingProblem();
                easyMatching.setNetTransaction(new Transaction(25, 25));
                easyMatching.setTransactions(new ArrayList<MatchableTransaction>());
                easyMatching.getTransactions().add(new MatchableTransaction(5, 5));
                easyMatching.getTransactions().add(new MatchableTransaction(5, 5));
                easyMatching.getTransactions().add(new MatchableTransaction(15, 15));
                easyMatching.getTransactions().add(new MatchableTransaction(5, 10));

                return easyMatching;
            }
        });

    }
}
