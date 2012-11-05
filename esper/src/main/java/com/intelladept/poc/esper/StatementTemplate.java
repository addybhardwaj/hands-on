package com.intelladept.poc.esper;

import com.espertech.esper.client.UpdateListener;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class StatementTemplate {

    private String statement;

    private UpdateListener updateListener;

    public StatementTemplate(String statement) {
        this.statement = statement;
    }

    public StatementTemplate(String statement, UpdateListener updateListener) {
        this.statement = statement;
        this.updateListener = updateListener;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public UpdateListener getUpdateListener() {
        return updateListener;
    }

    public void setUpdateListener(UpdateListener updateListener) {
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
