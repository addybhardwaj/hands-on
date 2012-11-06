package com.intelladept.poc.esper;

import com.intelladept.poc.esper.events.ReceivedEvent;
import com.intelladept.poc.esper.events.SentEvent;
import com.intelladept.poc.utils.MemoryUtils;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Test for simple sent and receive event tracking
 *
 * @author Aditya Bhardwaj
 */
public class EventTrackingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventTrackingTest.class);
    public static final String STATEMENT = "select rstream s.* from SentEvent.win:time(var_win sec) s " +
            " full outer join ReceivedEvent.win:time(var_win sec) r on s.transactionId = r.transactionId " +
            " where r.transactionId  is null ";
    public static final String PATTERN_STATEMENT = "select s.* from pattern [every s=SentEvent -> (timer:interval(var_win sec) and not ReceivedEvent(s.transactionId = transactionId)) ] ";

    private EsperTemplate esperTemplate;

    private CountingStatementListener expiredListener;

    private int delayInSecs = 30;

    public void before(String statement) throws NamingException, InterruptedException {
        esperTemplate = new EsperTemplate();
        esperTemplate.setPackageName("com.intelladept.poc.esper.events");

        Map<String, Object> variables = new HashMap<String, Object>();
        expiredListener = new CountingStatementListener("expiredListener");
        CountingStatementListener matchedListener  = new CountingStatementListener("matchedListener");;
        variables.put("var_win", delayInSecs);
        esperTemplate.setVariables(variables);
        esperTemplate.addStatementTemplate(statement, expiredListener);
//        esperTemplate.addStatementTemplate("select * from com.espertech.esper.client.metric.EngineMetric ", new CountingStatementListener("metrics"));
//        esperTemplate.addStatementTemplate("select * from com.espertech.esper.client.metric.StatementMetric ", new CountingStatementListener("metrics-stmt"));

        esperTemplate.startEngine();
    }

    @Test
    public void testSentAndReceive() throws InterruptedException, NamingException {
        delayInSecs = 5;
        before(STATEMENT);

        int i = 0;
        esperTemplate.sendEvent(new SentEvent("event " + i, 100));
        esperTemplate.sendEvent(new ReceivedEvent("event " + i, 100));

        TimeUnit.SECONDS.sleep(delayInSecs * 2);
        Assert.assertEquals(0, expiredListener.getCount());
    }

    @Test
    public void testSentOnly() throws InterruptedException, NamingException {
        delayInSecs = 5;
        before(STATEMENT);

        int i = 0;
        esperTemplate.sendEvent(new SentEvent("event " + i, 100));

        TimeUnit.SECONDS.sleep(delayInSecs * 2);
        Assert.assertEquals(1, expiredListener.getCount());
    }

    @Test
    public void testJoinStatementWithVolume() throws Exception {
        delayInSecs = 30;
        before(STATEMENT);

        String uuid = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();
        int max = 10000000;
        for (int i=0; i < max; i++) {
            esperTemplate.sendEvent(new SentEvent(uuid + "event " + i, 100));
            esperTemplate.sendEvent(new ReceivedEvent(uuid + "event " + i, 100));
            int batch = 10000;
            if (i% batch == 0) {
                if (i%(batch*20)==0) TimeUnit.SECONDS.sleep(5);
                LOGGER.info(" {} : Count {}", MemoryUtils.stats(), i);
            }
        }
        long end = System.currentTimeMillis();
        LOGGER.info("Time taken {} secs", (end - start) / 1000);
    }

    @Test
    public void testPatternSentAndReceive() throws InterruptedException, NamingException {
        delayInSecs = 5;
        before(PATTERN_STATEMENT);

        int i = 0;
        esperTemplate.sendEvent(new SentEvent("event " + i, 100));
        esperTemplate.sendEvent(new ReceivedEvent("event " + i, 100));

        TimeUnit.SECONDS.sleep(delayInSecs * 2);
        Assert.assertEquals(0, expiredListener.getCount());
    }

    @Test
    public void testPatternSentOnly() throws InterruptedException, NamingException {
        delayInSecs = 5;
        before(PATTERN_STATEMENT);

        int i = 0;
        esperTemplate.sendEvent(new SentEvent("event " + i, 100));

        TimeUnit.SECONDS.sleep(delayInSecs * 2);
        Assert.assertEquals(1, expiredListener.getCount());
    }

    @Test
    public void testPatternStatementWithVolume() throws Exception {
        delayInSecs = 45;
        before(PATTERN_STATEMENT);

        String uuid = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();
        int max = 300000;
        for (int i=0; i < max; i++) {
            esperTemplate.sendEvent(new SentEvent(uuid + "event " + i, 100));
            esperTemplate.sendEvent(new ReceivedEvent(uuid + "event " + i, 100));
            int batch = 10000;
            if (i% batch == 0) {
                LOGGER.info(" {} : Count {}", MemoryUtils.stats(), i);
            }
        }
        long end = System.currentTimeMillis();
        LOGGER.info("Time taken {} secs",  (end-start)/1000);

        while (true) {
            TimeUnit.SECONDS.sleep(5);
            Runtime.getRuntime().gc();
            LOGGER.info(" {} ", MemoryUtils.stats());
        }

    }
}
