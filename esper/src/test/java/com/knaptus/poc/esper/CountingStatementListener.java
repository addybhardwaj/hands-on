package com.knaptus.poc.esper;

import com.espertech.esper.client.*;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class CountingStatementListener implements StatementAwareUpdateListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountingStatementListener.class);

    private String name;

    private int count = 0;

    public CountingStatementListener(String name) {
        this.name = name;
    }

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement, EPServiceProvider epServiceProvider) {

        logEvent(newEvents, "New event", statement);
        logEvent(oldEvents, "Old event", statement);
    }

    private void logEvent(EventBean[] newEvents, final String desc, EPStatement statement) {
        if (newEvents != null) {
            LOGGER.info("{} >> Statement {}", name, statement.getText());
            for(EventBean eventBean : newEvents) {
                count ++;
                LOGGER.info("{} >> " + desc + " {}", name, eventBean);
                LOGGER.info(desc + " {}", ToStringBuilder.reflectionToString(eventBean));
            }
        }
    }

    public int getCount() {
        return count;
    }

}
