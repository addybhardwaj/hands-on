package com.knaptus.poc.esper;

import com.espertech.esper.client.StatementAwareUpdateListener;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class StatementTemplate {

    private String statement;

    private StatementAwareUpdateListener updateListener;

    public StatementTemplate(String statement) {
        this.statement = statement;
    }

    public StatementTemplate(String statement, StatementAwareUpdateListener updateListener) {
        this.statement = statement;
        this.updateListener = updateListener;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public StatementAwareUpdateListener getUpdateListener() {
        return updateListener;
    }

    public void setUpdateListener(StatementAwareUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public static List<StatementTemplate> createOnlyStatements(String... statements) {
        List<StatementTemplate> statementTemplates = new ArrayList<StatementTemplate>();
        if (statements != null) {
            for(String statement : statements) {
                StatementTemplate statementTemplate = new StatementTemplate(statement);
                statementTemplates.add(statementTemplate);

            }

        }

        return statementTemplates;
    }
}
