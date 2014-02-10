package com.knaptus.coding.handson.npcomplete.optaplanner;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.XmlSolverFactory;
import org.optaplanner.core.impl.solution.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs the planner.
 *
 * @author Aditya Bhardwaj
 */
public class PlannerRunner<S extends Score, T extends Solution<S>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlannerRunner.class);

    public T run(String contextFile, UnsolvedProblemBuilder<S, T> unsolvedProblemBuilder) {

        // Build the Solver
        SolverFactory solverFactory = new XmlSolverFactory(contextFile);
        Solver solver = solverFactory.buildSolver();

        // Load a problem with 400 computers and 1200 processes
        Solution<?> unsolvedProblem = unsolvedProblemBuilder.buildProblem();

        // Solve the problem
        solver.setPlanningProblem(unsolvedProblem);
        solver.solve();

        T solvedProblem = (T) solver.getBestSolution();

        // Display the result
        LOGGER.info("Solved problem [{}", solvedProblem);

        return solvedProblem;
    }

}
