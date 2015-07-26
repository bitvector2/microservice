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


public class ProductServer extends AbstractVerticle {
    public Cluster cluster;
    public Session session;
    private Logger logger;

    @Override
    public void start() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.ProductServer");//

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
        }, result -> {
            if (result.succeeded()) {
                // Start Controller
                ProductController productController = new ProductController(session);

                // Start HTTP Router
                Router router = Router.router(vertx);
                router.route().handler(BodyHandler.create());
                router.get("/products").handler(productController::handleGetAllProduct);
                router.get("/products/:productID").handler(productController::handleGetProductId);
                router.put("/products/:productID").handler(productController::handlePutProductId);
                router.post("/products").handler(productController::handlePostProduct);
                router.delete("/products/:productID").handler(productController::handleDeleteProductId);

                // Start HTTP Listener
                vertx.createHttpServer().requestHandler(router::accept).listen(
                        Integer.parseInt(System.getProperty("org.bitvector.microservice_test.listen-port")),
                        System.getProperty("org.bitvector.microservice_test.listen-address")
                );

                logger.info("Started a ProductServer...");
            }
        });
    }

    @Override
    public void stop() {
        session.closeAsync();
        cluster.closeAsync();
        logger.info("Stopped a ProductServer...");
    }

}
