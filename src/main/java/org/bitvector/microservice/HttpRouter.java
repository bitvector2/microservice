package org.bitvector.microservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;


public class HttpRouter extends AbstractVerticle {
    private Logger logger;
    private EventBus eb;
    private ObjectMapper jsonMapper;

    @Override
    public void start() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice.HttpRouter");
        eb = vertx.eventBus();
        jsonMapper = new ObjectMapper();

        // Start HTTP Router
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.head("/products").handler(this::handlePingProduct);
        router.get("/products").handler(this::handleGetAllProducts);
        router.get("/products/:productID").handler(this::handleGetProductId);
        router.put("/products/:productID").handler(this::handlePutProductId);
        router.post("/products").handler(this::handlePostProduct);
        router.delete("/products/:productID").handler(this::handleDeleteProductId);

        // Start HTTP Listener
        vertx.createHttpServer().requestHandler(router::accept).listen(
                Integer.parseInt(System.getProperty("bitvector.microservice.listen-port")),
                System.getProperty("bitvector.microservice.listen-address")
        );

        logger.info("Started a HttpRouter...");
    }

    @Override
    public void stop() {
        logger.info("Stopped a HttpRouter...");
    }

    private void handlePingProduct(RoutingContext routingContext) {
        DbMessage dbRequest = new DbMessage("handlePingProduct", null);

        eb.send("DbPersister", dbRequest, reply -> {
            if (reply.succeeded()) {
                DbMessage dbResponse = (DbMessage) reply.result().body();

                if (dbResponse.succeeded()) {
                    routingContext.response()
                            .setStatusCode(200)
                            .end();
                } else {
                    routingContext.response()
                            .setStatusCode(500)
                            .end();
                }
            }
        });
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

    private void handleGetProductId(RoutingContext routingContext) {
        ArrayList<String> params = new ArrayList<>();
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
        ArrayList<Product> params = new ArrayList<>();
        try {
            Product product = jsonMapper.readValue(routingContext.getBodyAsString(), Product.class);
            product.setId(Integer.parseInt(routingContext.request().getParam("productID")));
            params.add(product);
        } catch (IOException e) {
            logger.error("Failed to convert payload to JSON", e);
        }

        DbMessage dbRequest = new DbMessage("handlePutProductId", params);

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

    private void handlePostProduct(RoutingContext routingContext) {
        ArrayList<Product> params = new ArrayList<>();
        try {
            Product product = jsonMapper.readValue(routingContext.getBodyAsString(), Product.class);
            params.add(product);
        } catch (IOException e) {
            logger.error("Failed to convert payload to JSON", e);
        }

        DbMessage dbRequest = new DbMessage("handlePostProduct", params);

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

    private void handleDeleteProductId(RoutingContext routingContext) {
        ArrayList<String> params = new ArrayList<>();
        params.add(routingContext.request().getParam("productID"));

        DbMessage dbRequest = new DbMessage("handleDeleteProductId", params);

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

}
