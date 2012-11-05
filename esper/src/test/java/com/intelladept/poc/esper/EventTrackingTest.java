package com.intelladept.poc.esper;

import com.espertech.esper.client.UpdateListener;
import com.intelladept.poc.esper.events.ReceivedEvent;
import com.intelladept.poc.esper.events.SentEvent;
import com.intelladept.poc.utils.MemoryUtils;
import junit.framework.Assert;
import org.junit.Before;
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
public class EventTrackingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventTrackingTest.class);

    private EsperTemplate esperTemplate;

    private CountingStatementListener expiredListener;

    private int delayInSecs = 30;

    @Before
    public void before() throws NamingException, InterruptedException {
        esperTemplate = new EsperTemplate();
        esperTemplate.setPackageName("com.intelladept.poc.esper.events");

        Map<String, Object> variables = new HashMap<String, Object>();
        expiredListener = new CountingStatementListener("expiredListener");
        UpdateListener matchedListener  = new CountingStatementListener("matchedListener");;
        variables.put("var_win", delayInSecs);
        esperTemplate.setVariables(variables);
        esperTemplate.addStatementTemplate("select rstream s.* from SentEvent.win:time(var_win sec) s " +
                " full outer join ReceivedEvent.win:time(var_win sec) r on s.transactionId = r.transactionId " +
                " where r.transactionId  is null ", expiredListener);

//        esperTemplate.addStatementTemplate("select s.* from pattern [every s=SentEvent -> (timer:interval(var_win sec) and not ReceivedEvent(s.transactionId = transactionId)) ] ", expiredListener);

        esperTemplate.startEngine();
    }

    @Test
    public void testSentAndReceive() throws InterruptedException, NamingException {

        int i = 0;
        esperTemplate.sendEvent(new SentEvent("event " + i, 100));
        esperTemplate.sendEvent(new ReceivedEvent("event " + i, 100));

        TimeUnit.SECONDS.sleep(delayInSecs * 2);
        Assert.assertEquals(0, expiredListener.getCount());
    }

    @Test
    public void testSentOnly() throws InterruptedException, NamingException {

        int i = 0;
        esperTemplate.sendEvent(new SentEvent("event " + i, 100));

        TimeUnit.SECONDS.sleep(delayInSecs * 2);
        Assert.assertEquals(1, expiredListener.getCount());
    }

    @Test
    public void testWithVolume() throws Exception {
        String uuid = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();
        int max = 1000000;
        for (int i=0; i < max; i++) {
            esperTemplate.sendEvent(new SentEvent(uuid + "event " + i, 100));
            esperTemplate.sendEvent(new ReceivedEvent(uuid + "event " + i, 100));
            int batch = 10000;
            if (i% batch == 0) {
//                if (i%(batch*10)==0) Runtime.getRuntime().gc();
                LOGGER.info(" {} : Count {}", MemoryUtils.stats(), i);
            }
        }
        long end = System.currentTimeMillis();
        LOGGER.info("Time taken {} secs",  (end-start)/1000);
    }
}
