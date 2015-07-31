package org.bitvector.microservice_test;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;


public class Main {
    public static void main(String[] args) throws Exception {
        // Load settings
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties(System.getProperties());
        try (InputStream resourceStream = loader.getResourceAsStream("microservice_test.properties")) {
            props.load(resourceStream);
        }
        System.setProperties(props);

        // Start logging
        Logger logger = LoggerFactory.getLogger("org.bitvector.microservice_test.Main");
        logger.info("Starting Init...");

        // Start application
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle("org.bitvector.microservice_test.DbPersister", new DeploymentOptions().setWorker(true));
        vertx.deployVerticle("org.bitvector.microservice_test.HttpRouter");

        logger.info("Finished Init...");
    }
}
