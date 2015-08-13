package org.bitvector.microservice;

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
        try (InputStream resourceStream = loader.getResourceAsStream("microservice.properties")) {
            props.load(resourceStream);
        }
        System.setProperties(props);

        // Start logging
        Logger logger = LoggerFactory.getLogger("org.bitvector.microservice.Main");
        logger.info("Starting Init...");

        // Start application
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle("org.bitvector.microservice.IndexHandler", new DeploymentOptions().setWorker(true));
        vertx.deployVerticle("org.bitvector.microservice.DbHandler", new DeploymentOptions().setWorker(true));
        vertx.deployVerticle("org.bitvector.microservice.HttpHandler");

        logger.info("Finished Init...");
    }
}
