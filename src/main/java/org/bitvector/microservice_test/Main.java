package org.bitvector.microservice_test;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Properties;


public class Main {
    public static void main(String[] args) throws Exception {

        // Load settings
        String propFile = System.getProperty("user.dir") + System.getProperty("file.separator") + "microservice_test.properties";
        FileInputStream propStream = new FileInputStream(propFile);
        Properties props = new Properties(System.getProperties());
        props.load(propStream);
        System.setProperties(props);
        propStream.close();

        // Start logging
        Logger logger = LoggerFactory.getLogger("org.bitvector.microservice_test.Main");
        logger.info("Starting Init...");

        // Start clustered application node
        ClusterManager mgr = new HazelcastClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(mgr);

        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                Integer threadCount = Integer.parseInt(System.getProperty("bitvector.microservice_test.thread-count"));
                vertx.deployVerticle("org.bitvector.microservice_test.DbPersister", new DeploymentOptions().setWorker(true).setInstances(threadCount));
                vertx.deployVerticle("org.bitvector.microservice_test.HttpRouter", new DeploymentOptions().setInstances(threadCount));
            } else {
                logger.info("Failed Init...");
            }
        });

        logger.info("Finished Init...");
    }
}
