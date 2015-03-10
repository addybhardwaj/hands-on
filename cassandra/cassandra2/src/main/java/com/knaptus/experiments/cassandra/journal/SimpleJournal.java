package com.knaptus.experiments.cassandra.journal;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.base.Stopwatch;
import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class SimpleJournal {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleJournal.class);
    public static String DEFAULT;

    final SimpleClient client;
    final DataCreator dataCreator;

    public SimpleJournal(SimpleClient client) {
        this.client = client;
        dataCreator = new DataCreator();
    }

    private static String getShard(UUID uuid) {
        return new LocalDateTime(UUIDs.unixTimestamp(uuid)).toString("yyyyMMdd");
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        DEFAULT = "default" + 1;
        LOGGER.info("Sample at [{}] was [{}]", getShard(UUIDs.timeBased()));
        SimpleClient client = new SimpleClient();
        client.connect("127.0.0.1");

        SimpleJournal journal = new SimpleJournal(client);
//        journal.dropTable();
        journal.createTable();

        journal.generateLoad();
//        Writer writer = journal.createFile();

        try {
//            journal.queryAll(writer);
        } finally {
//            writer.flush();
//            writer.close();
        }
        client.close();
    }

    private FileWriter createFile() throws IOException {
        File file = new File("./target/"+DEFAULT+".dat");
        if (!file.exists()) {
            FileUtils.forceMkdir(file.getParentFile());//.mkdirs();
            file.createNewFile();
        }
        return new FileWriter(file, false);
    }

    public void queryAll(Writer writer) throws IOException {
        String statement = "select * from journals." + DEFAULT + " where name = 'txn' and date = ?";
        List<String> shards = Arrays.asList("201409152353", "201409152354", "201409152355", "201409152356", "201409152357");


        int totalCount = 0;
        for (String shard : shards) {
            int count = 0;
            Iterator<Row> data = client.execute(statement, shard).iterator();
            while (data.hasNext()) {
                Row row = data.next();
//                IOUtils.writeLines(Arrays.asList(row.toString()), "\n", writer);
                count ++;
                totalCount ++;
                if (count % 1000 == 0) {
                    LOGGER.info("Shard [{}] has [{}] messages, still counting", shard, count);
                }
            }
            LOGGER.info("Shard [{}] has [{}] messages", shard, count);
        }
        LOGGER.info("All shards have [{}] messages", totalCount);
    }

    private void getInFlightMessage() {
        //1. find the last outbound
        //1.1 find most recent partition
        //select distinct date, name from journals.default1 where token(name, date) >= token('txn-out', '20140911') and token(name, date) < token('txn-out', '20141010');
        //1.2 find last committed state
        //select * from journals.default1 where name = 'txn-out' and date = '20140917' and id < now() order by id desc limit 1;

        //2. read inbounds using last outbound id (using paging)
        //select * from journals.default1 where name = 'txn-in' and date = '20140917' and id > a2dc2e40-3df8-11e4-805e-e33b21546550 ;
    }

    private void getFeedOfStartOrFinish() {
        //1. find the last id for the feed
        //select * from journals.default1 where name = 'txn-feedname' and date = '20140915' and id < maxTimeuuid('current time') order by id desc limit 1 ALLOW FILTERING;

        //2. poll for results every 100ms

        //3. process in batches and persist batches
    }

    public void generateLoad() throws InterruptedException {
        LOGGER.info("Sample at [{}] was [{}]", DEFAULT, dataCreator.createPayload());
        int totalLoad = 10000;
        int threads = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        LOGGER.info("Starting journaling");
        Stopwatch stopwatch = new Stopwatch(); //.createUnstarted();
        stopwatch.start();
        for (int i=0; i < threads; i++) {
            executorService.execute(new DataJournaller(dataCreator, client, totalLoad / threads));
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.MINUTES);

        stopwatch.stop();
        LOGGER.info("Finished journalling. Total time [{}] secs. [{}] msg/secs", stopwatch.elapsed(TimeUnit.SECONDS), totalLoad/stopwatch.elapsed(TimeUnit.SECONDS));

    }


    public static class DataJournaller implements Runnable {

        private final DataCreator dataCreator;
        private final SimpleClient client;
        private final int workLoad;

        public DataJournaller(DataCreator dataCreator, SimpleClient client, int workLoad) {
            this.dataCreator = dataCreator;
            this.client = client;
            this.workLoad = workLoad;
        }

        @Override
        public void run() {

            for(int i=0; i< workLoad; i++) {
                boolean hasToJournal = true;
                while (hasToJournal) {
                    try {
                        journal();
                        hasToJournal = false;
                    } catch (Exception e) {
                        try {
                            TimeUnit.SECONDS.sleep(2);
                        } catch (InterruptedException e1) {
                            LOGGER.error("Interrupted thread [{}] got exception ", Thread.currentThread().getName());
                        }
                        LOGGER.error("Thread [{}] got exception ", Thread.currentThread().getName());
                    }
                }
                if (i%(workLoad/10) == 0) {
                    LOGGER.info("Thread [{}], has journalled [{}] message", Thread.currentThread().getName(), i);
                }
            }
            LOGGER.info("Finishing Thread [{}]", Thread.currentThread().getName());
        }

        private void journal() {

            String payload = dataCreator.createPayload();
            UUID id = UUIDs.timeBased();
//            long timestamp = UUIDs.unixTimestamp(id);
//            new LocalDateTime(timestamp).toString();
            Insert insert = QueryBuilder.insertInto("journals", DEFAULT)
                    .value("name", "txn-in")
                    .value("date", getShard(id))
                    .value("id", id)
//                    .value("type", "inbound")
                    .value("payload", payload)
                    ;
            client.execute(insert);

            insert = QueryBuilder.insertInto("journals", DEFAULT)
                    .value("name", "txn-out")
                    .value("date", getShard(id))
                    .value("id", id)
//                    .value("type", "outbound")
                    .value("payload", payload)
            ;

//            client.execute(insert);
        }
    }

    public void dropTable() {
        client.execute("drop table IF EXISTS journals." + DEFAULT + ";");
    }

    public void createTable() {
        client.execute("create keyspace IF NOT EXISTS journals with replication = {'class':'SimpleStrategy', 'replication_factor':1};");

        client.execute("create table IF NOT EXISTS journals." + DEFAULT + "(" +
                " name text, " +
                " date text, " +
                " id uuid, " +
//                " type text, " +
//                " status int, " +
                " payload blob, " +
//                " logs list<text>, " +
//                " headers map<text,text>, " +
                " PRIMARY KEY ((name, date), id) " +
                ") " +
                "WITH compression = { 'sstable_compression' : 'LZ4Compressor' };");
    }
}

/**
 select * from journals.default where id = b5aa55e0-1771-11e4-8895-0728b4274ba6 ALLOW FILTERING;
 select * from journals.default1 where id < maxTimeuuid('2014-09-16') ALLOW FILTERING; /// can cause Out of memory .. too inefficient
 select * from journals.default where date = '2014-07-30' and name ='txn';
 select * from journals.default where id = b5aa55e0-1771-11e4-8895-0728b4274ba6 and type='inbound' ALLOW FILTERING;
 select * from journals.default1 where name = 'txn' and date = '201409152356' and id < maxTimeuuid('2014-09-16') limit 1 ALLOW FILTERING;
**/