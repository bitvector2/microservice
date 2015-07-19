package org.bitvector.test;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws Exception {

        // Load settings
        String propFile = System.getProperty("user.dir") + System.getProperty("file.separator") + "microservice-test.properties";
        FileInputStream propStream = new FileInputStream(propFile);
        Properties props = new Properties(System.getProperties());
        props.load(propStream);
        System.setProperties(props);
        propStream.close();

        // Start logging
        Logger logger = LoggerFactory.getLogger("org.bitvector.test.Main");
        logger.info("Starting up...");

        // Start application
        Integer threadCount = Integer.parseInt(System.getProperty("org.bitvector.test.thread-count"));
        Vertx vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions().setInstances(threadCount);
        vertx.deployVerticle("org.bitvector.test.RESTServer", options);

    }

}
