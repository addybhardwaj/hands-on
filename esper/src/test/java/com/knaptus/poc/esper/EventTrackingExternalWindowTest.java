package com.knaptus.poc.esper;

import com.espertech.esper.client.StatementAwareUpdateListener;
import com.knaptus.poc.esper.events.ReceivedEvent;
import com.knaptus.poc.esper.events.SentEvent;
import com.knaptus.poc.utils.MemoryUtils;
import junit.framework.Assert;
import org.junit.After;
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
    public static final String STATEMENT_1 = "select rstream s.* from SentEvent.win:ext_timed(timestamp, var_win sec) s " +
            " full outer join ReceivedEvent.win:ext_timed(timestamp, var_win sec) r on s.transactionId = r.transactionId " +
            " where r.transactionId  is null ";

    public static final String STATEMENT_2 = "select rstream s.* from SentEvent.ext:time_order(timestamp, var_win sec) s " +
            " left outer join ReceivedEvent.ext:time_order(timestamp, var_win sec) r on s.transactionId = r.transactionId " +
            " where r.transactionId  is null ";

    public static final String STATEMENT = "select irstream s.* from SentEvent.ext:time_order(timestamp, var_win sec) s " +
            " left outer join ReceivedEvent.ext:time_order(timestamp, var_win sec) r on s.transactionId = r.transactionId " +
            " where r.transactionId  is null ";

    //window with sent
    //window with received
    //insert into stream of matched
    //delete from sent and received when in matched
    //
    private EsperTemplate esperTemplate;

    private CountingStatementListener expiredListener;

    private int delayInSecs = 5;

    public void before(String statement) throws NamingException, InterruptedException {
        esperTemplate = new EsperTemplate();
        esperTemplate.setPackageName("com.knaptus.poc.esper.events");

        Map<String, Object> variables = new HashMap<String, Object>();
        expiredListener = new CountingStatementListener("expiredListener");
        StatementAwareUpdateListener matchedListener  = new CountingStatementListener("matchedListener");;
        variables.put("var_win", delayInSecs);
        esperTemplate.setVariables(variables);
        esperTemplate.addStatements(
                "create window SentWindow.ext:time_order(timestamp, var_win sec) as SentEvent",
                "create window ReceivedWindow.ext:time_order(timestamp, var_win sec) as ReceivedEvent",
                "insert into SentWindow select * from SentEvent",
                "insert into ReceivedWindow select * from ReceivedEvent",
                "insert into MatchedEvent select s.* from SentWindow s, ReceivedWindow r where s.transactionId = r.transactionId ",
                "on MatchedEvent m delete from SentWindow s where s.transactionId = m.transactionId ",
                "on MatchedEvent m delete from ReceivedWindow r where r.transactionId = m.transactionId ",
                "insert rstream into SentRemove select * from SentWindow s"

        );

        esperTemplate.addStatementTemplate("select s.* from SentRemove.std:lastevent() s full outer join MatchedEvent.std:lastevent() m on s.transactionId = m.transactionId where m.transactionId is null", expiredListener);

        esperTemplate.startEngine();
    }

    @After
    public void after() {
        esperTemplate.stopEngine();
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
        delayInSecs = 20;
        before(STATEMENT);
        String uuid = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();
        int max = 1000000;
        for (int i=0; i < max; i++) {
            esperTemplate.sendEvent(new SentEvent(uuid + "event " + i, System.currentTimeMillis()));
            esperTemplate.sendEvent(new ReceivedEvent(uuid + "event " + i, System.currentTimeMillis()));
            int batch = 10000;
            if (i% batch == 0) {
                LOGGER.info(" {} : Count {}", MemoryUtils.stats(), i);
            }
        }
        long end = System.currentTimeMillis();
        LOGGER.info("Time taken {} secs",  (end-start)/1000);


    }
}
