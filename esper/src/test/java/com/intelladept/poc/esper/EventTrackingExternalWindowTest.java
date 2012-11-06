package com.intelladept.poc.esper;

import com.espertech.esper.client.UpdateListener;
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
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class EventTrackingExternalWindowTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventTrackingExternalWindowTest.class);
    public static final String STATEMENT = "select rstream s.* from SentEvent.ext:time_order(timestamp, var_win sec) s " +
            " full outer join ReceivedEvent.ext:time_order(timestamp, var_win sec) r on s.transactionId = r.transactionId " +
            " where r.transactionId  is null ";

    private EsperTemplate esperTemplate;

    private CountingStatementListener expiredListener;

    private int delayInSecs = 5;

    public void before(String statement) throws NamingException, InterruptedException {
        esperTemplate = new EsperTemplate();
        esperTemplate.setPackageName("com.intelladept.poc.esper.events");

        Map<String, Object> variables = new HashMap<String, Object>();
        expiredListener = new CountingStatementListener("expiredListener");
        UpdateListener matchedListener  = new CountingStatementListener("matchedListener");;
        variables.put("var_win", delayInSecs);
        esperTemplate.setVariables(variables);
        esperTemplate.addStatementTemplate(statement, expiredListener);

        esperTemplate.startEngine();
    }

    @Test
    public void testSentAndReceive() throws InterruptedException, NamingException {
        delayInSecs = 5;
        before(STATEMENT);
        int i = 0;
        esperTemplate.sendEvent(new SentEvent("event " + i, System.currentTimeMillis()));
        esperTemplate.sendEvent(new ReceivedEvent("event " + i, System.currentTimeMillis()));

        TimeUnit.SECONDS.sleep(delayInSecs * 2);
        Assert.assertEquals(0, expiredListener.getCount());
    }

    @Test
    public void testSentOnly() throws InterruptedException, NamingException {
        delayInSecs = 5;
        before(STATEMENT);

        int i = 0;
        esperTemplate.sendEvent(new SentEvent("event " + i, System.currentTimeMillis()));

        TimeUnit.SECONDS.sleep(delayInSecs * 2);
        Assert.assertEquals(1, expiredListener.getCount());
    }

    @Test
    public void testAlreadyExpiredSentEventAndReceiveOnly() throws InterruptedException, NamingException {
        delayInSecs = 5;
        before(STATEMENT);

        int i = 0;
        esperTemplate.sendEvent(new SentEvent("event " + i, System.currentTimeMillis()-delayInSecs*1000*2));
        esperTemplate.sendEvent(new ReceivedEvent("event " + i, System.currentTimeMillis()));

        TimeUnit.SECONDS.sleep(delayInSecs / 2);
        Assert.assertEquals(1, expiredListener.getCount());
    }

    @Test
    public void testAboutToExpireSentEventAndReceiveOnly() throws InterruptedException, NamingException {
        delayInSecs = 5;
        before(STATEMENT);

        int i = 0;
        esperTemplate.sendEvent(new SentEvent("event " + i, System.currentTimeMillis()-(delayInSecs-1)*1000));
        esperTemplate.sendEvent(new ReceivedEvent("event " + i, System.currentTimeMillis()));

        TimeUnit.SECONDS.sleep(delayInSecs * 2);
        Assert.assertEquals(0, expiredListener.getCount());
    }

    @Test
    public void testWithVolume() throws Exception {
        delayInSecs = 30;
        before(STATEMENT);
        String uuid = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();
        int max = 10000000;
        for (int i=0; i < max; i++) {
            esperTemplate.sendEvent(new SentEvent(uuid + "event " + i, System.currentTimeMillis()));
            esperTemplate.sendEvent(new ReceivedEvent(uuid + "event " + i, System.currentTimeMillis()));
            int batch = 10000;
            if (i% batch == 0) {
//                if (i%(batch*20)==0) TimeUnit.SECONDS.sleep(5);
                LOGGER.info(" {} : Count {}", MemoryUtils.stats(), i);
            }
        }
        long end = System.currentTimeMillis();
        LOGGER.info("Time taken {} secs",  (end-start)/1000);
    }
}
