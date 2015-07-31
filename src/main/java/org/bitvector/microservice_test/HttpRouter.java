package org.bitvector.microservice_test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public class HttpRouter extends AbstractVerticle {
    private Logger logger;
    private EventBus eb;
    private ObjectMapper jsonMapper;

    @Override
    public void start() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.HttpRouter");
        eb = vertx.eventBus();
        jsonMapper = new ObjectMapper();

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

                if (dbResponse.succeeded()) {
                    String jsonString = null;
                    try {
                        jsonString = jsonMapper.writeValueAsString(dbResponse.getResults());
                    } catch (JsonProcessingException e) {
                        logger.error("Failed to convert Results to JSON", e);
                    }
                    routingContext.response()
                            .putHeader("content-type", "application/json")
                            .end(jsonString);
                } else {
                    routingContext.response()
                            .setStatusCode(500)
                            .end();
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void handleGetProductId(RoutingContext routingContext) {
        ArrayList params = new ArrayList();
        params.add(routingContext.request().getParam("productID"));

        DbMessage dbRequest = new DbMessage("handleGetProductId", params);

        eb.send("DbPersister", dbRequest, reply -> {
            if (reply.succeeded()) {
                DbMessage dbResponse = (DbMessage) reply.result().body();

                if (dbResponse.succeeded()) {
                    String jsonString = null;
                    try {
                        jsonString = jsonMapper.writeValueAsString(dbResponse.getResults());
                    } catch (JsonProcessingException e) {
                        logger.error("Failed to convert Results to JSON", e);
                    }
                    routingContext.response()
                            .putHeader("content-type", "application/json")
                            .end(jsonString);
                } else {
                    routingContext.response()
                            .setStatusCode(500)
                            .end();
                }
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
