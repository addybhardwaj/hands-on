package com.knaptus.coding.handson.npcomplete.optaplanner.matcher;

import java.util.List;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class ProblemStatement {

    private Scenario scenario;
    private List<Transaction> dataSet;
    private Transaction problem;
    private List<Transaction> solution;

    public ProblemStatement() {}

    public ProblemStatement(List<Transaction> dataSet, Transaction problem, List<Transaction> solution) {
        this.dataSet = dataSet;
        this.problem = problem;
        this.solution = solution;
    }

    public List<Transaction> getDataSet() {
        return dataSet;
    }

    public Transaction getProblem() {
        return problem;
    }

    public List<Transaction> getSolution() {
        return solution;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public void setDataSet(List<Transaction> dataSet) {
        this.dataSet = dataSet;
    }

    public void setProblem(Transaction problem) {
        this.problem = problem;
    }

    public void setSolution(List<Transaction> solution) {
        this.solution = solution;
    }
}
