package com.knaptus.experiments.cassandra.journal;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.utils.UUIDs;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class SimpleJournal {

    final SimpleClient client;

    public SimpleJournal(SimpleClient client) {
        this.client = client;
    }

    public static void main(String[] args) {
        SimpleClient client = new SimpleClient();
        client.connect("127.0.0.1");

        SimpleJournal journal = new SimpleJournal(client);
        journal.dropTable();
        journal.createTable();

        journal.putData();
        client.close();
    }

    private void putData() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("inbound1", "1");
        headers.put("inbound2", "2");
        Insert insert = QueryBuilder.insertInto("journals", "default")
                .value("name", "txn")
                .value("date", DateTime.now().toLocalDate().toString())
                .value("id", UUIDs.timeBased())
                .value("type", "inbound")
                .value("status", 0)
                .value("payload", "message received was 1")
                .value("logs", Arrays.asList("log1", "log2"))
                .value("headers", headers);
        client.session().execute(insert);

        UUID id = UUIDs.timeBased();
        insert = QueryBuilder.insertInto("journals", "default")
                .value("name", "txn")
                .value("date", DateTime.now().toLocalDate().toString())
                .value("id", id)
                .value("type", "inbound")
                .value("status", 0)
                .value("payload", "message received was 2")
                .value("logs", Arrays.asList("log1", "log2"))
                .value("headers", headers);
        client.session().execute(insert);
        insert = QueryBuilder.insertInto("journals", "default")
                .value("name", "txn")
                .value("date", DateTime.now().toLocalDate().toString())
                .value("id", id)
                .value("type", "outbound")
                .value("status", 1)
                .value("payload", "message sent was 2")
                .value("logs", Arrays.asList("sent log1", "sent log2"))
                .value("headers", headers);
        client.session().execute(insert);

    }

    public void dropTable() {
        client.session().execute("drop table IF EXISTS journals.default;");
    }

    public void createTable() {
        client.session().execute("create keyspace IF NOT EXISTS journals with replication = {'class':'SimpleStrategy', 'replication_factor':1};");

        client.session().execute("create table IF NOT EXISTS journals.default (" +
                " name text, " +
                " date text, " +
                " id uuid, " +
                " type text, " +
                " status int, " +
                " payload text, " +
                " logs list<text>, " +
                " headers map<text,text>, " +
                " PRIMARY KEY ((name, date), id, type, status) " +
                ");");
    }
}

/**
 select * from journals.default where id = b5aa55e0-1771-11e4-8895-0728b4274ba6 ALLOW FILTERING;
 select * from journals.default where id > maxTimeuuid('2013-01-01') ALLOW FILTERING;
 select * from journals.default where date = '2014-07-30' and name ='txn';
 select * from journals.default where id = b5aa55e0-1771-11e4-8895-0728b4274ba6 and type='inbound' ALLOW FILTERING;
**/