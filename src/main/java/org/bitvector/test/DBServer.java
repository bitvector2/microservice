package org.bitvector.test;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import io.vertx.core.AbstractVerticle;


public class DBServer extends AbstractVerticle {
    private Cluster cluster;
    private Session session;

    @Override
    public void start() {
        vertx.executeBlocking(future -> {
            Cluster c = Cluster.builder()
                    .addContactPoint(System.getProperty("org.bitvector.test.db-node"))
                    .build();
            future.complete(c);
        }, res -> {
            this.cluster = (Cluster) res.result();
        });

        vertx.executeBlocking(future -> {
            Session s = cluster.connect();
            future.complete(s);
        }, res -> {
            this.session = (Session) res.result();
        });


    }

    @Override
    public void stop() {
        session.closeAsync();
        cluster.closeAsync();
    }

}