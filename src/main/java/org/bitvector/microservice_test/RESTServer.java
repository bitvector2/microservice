package org.bitvector.microservice_test;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.ConstantReconnectionPolicy;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RESTServer extends AbstractVerticle {
    public Cluster cluster;
    public Session session;
    private Logger logger;

    @Override
    public void start() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.RESTServer");//

        vertx.executeBlocking(future -> {
            // Start Database
            String[] nodes = System.getProperty("org.bitvector.microservice_test.db-nodes").split(",");
            cluster = Cluster.builder()
                    .addContactPoints(nodes)
                    .withRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE)
                    .withReconnectionPolicy(new ConstantReconnectionPolicy(100L))
                    .build();
            session = cluster.connect();
            future.complete();
        }, res -> {
            if (res.succeeded()) {
                // Start REST Collection
                Product productColl = new Product(session);

                // Start HTTP Router
                Router router = Router.router(vertx);
                router.route().handler(BodyHandler.create());
                router.get("/products").handler(productColl::handleListProducts);

                // Start HTTP Listener
                vertx.createHttpServer().requestHandler(router::accept).listen(
                        Integer.parseInt(System.getProperty("org.bitvector.microservice_test.listen-port")),
                        System.getProperty("org.bitvector.microservice_test.listen-address")
                );

                logger.info("Started a RESTServer...");
            }
        });
    }

    @Override
    public void stop() {
        session.closeAsync();
        cluster.closeAsync();
        logger.info("Stopped a RESTServer...");
    }

}

