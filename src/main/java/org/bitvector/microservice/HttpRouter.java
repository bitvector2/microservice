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
        router.get("/products").handler(this::handleGetAllProducts);
        router.get("/products/:productID").handler(this::handleGetProductById);
        router.put("/products/:productID").handler(this::handlePutProductById);
        router.post("/products").handler(this::handlePostProduct);
        router.delete("/products/:productID").handler(this::handleDeleteProductById);

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


    private void handleGetAllProducts(RoutingContext routingContext) {
        DbMessage dbRequest = new DbMessage("getAllProducts", null);

        eb.send("DbProxy", dbRequest, reply -> {
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

    private void handleGetProductById(RoutingContext routingContext) {
        ArrayList<String> params = new ArrayList<>();
        params.add(routingContext.request().getParam("productID"));

        DbMessage dbRequest = new DbMessage("getProductById", params);

        eb.send("DbProxy", dbRequest, reply -> {
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

    private void handlePutProductById(RoutingContext routingContext) {
        ArrayList<Product> params = new ArrayList<>();
        try {
            Product product = jsonMapper.readValue(routingContext.getBodyAsString(), Product.class);
            product.setId(Integer.parseInt(routingContext.request().getParam("productID")));
            params.add(product);
        } catch (IOException e) {
            logger.error("Failed to convert payload to JSON", e);
        }

        DbMessage dbRequest = new DbMessage("updateProduct", params);

        eb.send("DbProxy", dbRequest, reply -> {
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

    private void handlePostProduct(RoutingContext routingContext) {
        ArrayList<Product> params = new ArrayList<>();
        try {
            Product product = jsonMapper.readValue(routingContext.getBodyAsString(), Product.class);
            params.add(product);
        } catch (IOException e) {
            logger.error("Failed to convert payload to JSON", e);
        }

        DbMessage dbRequest = new DbMessage("addProduct", params);

        eb.send("DbProxy", dbRequest, reply -> {
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

    private void handleDeleteProductById(RoutingContext routingContext) {
        ArrayList<String> params1 = new ArrayList<>();
        params1.add(routingContext.request().getParam("productID"));

        DbMessage dbRequest1 = new DbMessage("getProductById", params1);

        eb.send("DbProxy", dbRequest1, reply1 -> {
            if (reply1.succeeded()) {
                DbMessage dbResponse1 = (DbMessage) reply1.result().body();

                if (dbResponse1.succeeded()) {
                    Product product = (Product) dbResponse1.getResults().get(0);
                    ArrayList<Product> params2 = new ArrayList<>();
                    params2.add(product);

                    DbMessage dbRequest2 = new DbMessage("deleteProduct", params2);

                    eb.send("DbProxy", dbRequest2, reply2 -> {
                        if (reply2.succeeded()) {
                            routingContext.response()
                                    .setStatusCode(200)
                                    .end();
                        } else {
                            routingContext.response()
                                    .setStatusCode(500)
                                    .end();
                        }
                    });

                } else {
                    routingContext.response()
                            .setStatusCode(500)
                            .end();
                }
            }
        });

    }

}
