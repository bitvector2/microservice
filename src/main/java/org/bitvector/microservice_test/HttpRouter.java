package org.bitvector.microservice_test;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpRouter extends AbstractVerticle {
    private Logger logger;
    private EventBus eb;

    @Override
    public void start() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.HttpRouter");
        eb = vertx.eventBus();

        // Start HTTP Router
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/products").handler(this::handleGetAllProducts);
        router.get("/products/:productID").handler(this::handleGetProductId);
        router.put("/products/:productID").handler(this::handlePutProductId);
        router.post("/products").handler(this::handlePostProduct);
        router.delete("/products/:productID").handler(this::handleDeleteProductId);

        // Start HTTP Listener
        vertx.createHttpServer().requestHandler(router::accept).listen(
                Integer.parseInt(System.getProperty("bitvector.microservice_test.listen-port")),
                System.getProperty("bitvector.microservice_test.listen-address")
        );

        logger.info("Started a HttpRouter...");
    }

    @Override
    public void stop() {
        logger.info("Stopped a HttpRouter...");
    }

    private void handleGetAllProducts(RoutingContext routingContext) {

        DbMessage dbRequest = new DbMessage("handleGetAllProducts", null);
        eb.send("DbPersister", dbRequest, reply -> {
            if (reply.succeeded()) {
                DbMessage dbResponse = (DbMessage) reply.result().body();

                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .end(dbResponse.getResult());
            } else {
                routingContext.response()
                        .setStatusCode(500)
                        .end();
            }
        });

    }

    private void handleGetProductId(RoutingContext routingContext) {
        String productID = routingContext.request().getParam("productID");

        DbMessage dbRequest = new DbMessage("handleGetProductId", productID);
        eb.send("DbPersister", dbRequest, reply -> {
            if (reply.succeeded()) {
                DbMessage dbResponse = (DbMessage) reply.result().body();

                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .end(dbResponse.getResult());
            } else {
                routingContext.response()
                        .setStatusCode(500)
                        .end();
            }
        });

    }

    private void handlePutProductId(RoutingContext routingContext) {
        // FIXME
    }

    private void handlePostProduct(RoutingContext routingContext) {
        // FIXME
    }

    private void handleDeleteProductId(RoutingContext routingContext) {
        // FIXME
    }

}

