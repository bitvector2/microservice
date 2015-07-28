package org.bitvector.microservice_test;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
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

        // Start application
        Integer threadCount = Integer.parseInt(System.getProperty("org.bitvector.microservice_test.thread-count"));
        Vertx vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions().setInstances(threadCount);
        vertx.deployVerticle("org.bitvector.microservice_test.DbPersister", options);
        vertx.deployVerticle("org.bitvector.microservice_test.HttpServer", options);

        logger.info("Finished Init...");

    }
}
