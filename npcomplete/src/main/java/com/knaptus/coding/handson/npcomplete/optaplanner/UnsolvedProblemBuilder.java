package com.knaptus.coding.handson.npcomplete.optaplanner;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.solution.Solution;

/**
 * Builds the unsolved problem.
 *
 * @author Aditya Bhardwaj
 */
public interface UnsolvedProblemBuilder<S extends Score, T extends Solution<S>> {

    T buildProblem();
}
