package com.knaptus.experiments.cassandra.journal;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.ExponentialReconnectionPolicy;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class SimpleClient {

    private Cluster cluster;
    private Session session;

    public void connect(String node) {
        cluster = Cluster.builder()
                .addContactPoint(node)
                .withCompression(ProtocolOptions.Compression.LZ4)
                .withReconnectionPolicy(new ExponentialReconnectionPolicy(100, 10000))
//                .withPoolingOptions(new PoolingOptions())
                .build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n",
                metadata.getClusterName());
        for ( Host host : metadata.getAllHosts() ) {
            System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
                    host.getDatacenter(), host.getAddress(), host.getRack());
        }

        this.session = cluster.connect();
    }


    public ResultSet execute(String statement, Object... params) {
        SimpleStatement simpleStatement = new SimpleStatement(statement, params);
        simpleStatement.setFetchSize(500);
        return this.session.execute(simpleStatement);
    }

    public ResultSet execute(Statement statement) {
        return this.session.execute(statement);
    }


    public void close() {
        cluster.close();
    }
}
