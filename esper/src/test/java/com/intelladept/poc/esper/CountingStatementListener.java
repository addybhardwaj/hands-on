package com.intelladept.poc.esper;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class CountingStatementListener implements UpdateListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountingStatementListener.class);

    private String name;

    private int count = 0;

    public CountingStatementListener(String name) {
        this.name = name;
    }

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {

        logEvent(newEvents, "New event");
        logEvent(oldEvents, "Old event");
    }

    private void logEvent(EventBean[] newEvents, final String desc) {
        if (newEvents != null) {
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
