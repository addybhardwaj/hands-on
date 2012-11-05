package com.intelladept.poc.esper;

import com.espertech.esper.client.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class EsperTemplate extends Thread {

    private AtomicBoolean started = new AtomicBoolean(false);
    private String packageName;

    private Map<String, Object> variables = new HashMap<String, Object>();

    private List<StatementTemplate> statementTemplates = new ArrayList<StatementTemplate>();

    private EPServiceProvider epService;

    public void startEngine() throws InterruptedException {
        this.start();
        while(started.get() == false) {
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }

    public void run()  {

        Configuration config = new Configuration();
        if (packageName != null) {
            config.addEventTypeAutoName(packageName);
        }
        epService = EPServiceProviderManager.getDefaultProvider(config);

        for(Map.Entry<String, Object> entry : variables.entrySet()) {
            epService.getEPAdministrator().getConfiguration().addVariable(entry.getKey(), entry.getValue().getClass(),entry.getValue());
        }

        if (statementTemplates != null) {
            for (StatementTemplate statementTemplate : statementTemplates) {
                EPStatement statement = epService.getEPAdministrator().createEPL(statementTemplate.getStatement());
                if (statementTemplate.getUpdateListener() != null) {
                    statement.addListener(statementTemplate.getUpdateListener());
                }
            }
        }

        started.set(true);

    }

    public void stopEngine() {
        epService.destroy();
    }

    public void sendEvent(Object event) {
        epService.getEPRuntime().sendEvent(event);
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setStatementTemplates(List<StatementTemplate> statementTemplates) {
        this.statementTemplates.addAll(statementTemplates);
    }

    public EsperTemplate addStatementTemplate(String statement, UpdateListener updateListener) {
        this.statementTemplates.add(new StatementTemplate(statement, updateListener));
        return this;
    }

    public EsperTemplate addStatements(String... statements) {
        this.statementTemplates.addAll(StatementTemplate.createOnlyStatements(statements));
        return this;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables.putAll(variables);
    }
}
