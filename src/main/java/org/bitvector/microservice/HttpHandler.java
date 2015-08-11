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

public class HttpHandler extends AbstractVerticle {
    private Logger logger;
    private EventBus eb;
    private ObjectMapper jsonMapper;

    @Override
    public void start() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice.HttpHandler");
        eb = vertx.eventBus();
        jsonMapper = new ObjectMapper();

        // Start HTTP Router
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/products").handler(this::getAllProducts);
        router.get("/products/:ID").handler(this::getProductById);
        router.put("/products/:ID").handler(this::putProductById);
        router.post("/products").handler(this::postProduct);
        router.delete("/products/:ID").handler(this::deleteProductById);

        // Start HTTP Listener
        vertx.createHttpServer().requestHandler(router::accept).listen(
                Integer.parseInt(System.getProperty("bitvector.microservice.listen-port")),
                System.getProperty("bitvector.microservice.listen-address")
        );

        logger.info("Started a HttpHandler...");
    }

    @Override
    public void stop() {
        logger.info("Stopped a HttpHandler...");
    }


    private void getAllProducts(RoutingContext routingContext) {
        DbMessage dbRequest = new DbMessage("getAllProducts");

        eb.send("DbHandler", dbRequest, reply -> {
            if (reply.succeeded()) {
                DbMessage dbResponse = (DbMessage) reply.result().body();

                if (dbResponse.getSuccess()) {
                    String jsonString = null;
                    try {
                        jsonString = jsonMapper.writeValueAsString(dbResponse.getResult());
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

    private void getProductById(RoutingContext routingContext) {
        Integer id = Integer.parseInt(routingContext.request().getParam("ID"));
        DbMessage dbRequest = new DbMessage("getProductById", id);

        eb.send("DbHandler", dbRequest, reply -> {
            if (reply.succeeded()) {
                DbMessage dbResponse = (DbMessage) reply.result().body();

                if (dbResponse.getSuccess()) {
                    String jsonString = null;
                    try {
                        jsonString = jsonMapper.writeValueAsString(dbResponse.getResult());
                    } catch (JsonProcessingException e) {
                        logger.error("Failed to convert Results to JSON", e);
                    }
                    routingContext.response()
                            .putHeader("content-type", "application/json")
                            .end(jsonString);
                } else {
                    routingContext.response()
                            .setStatusCode(404)
                            .end();
                }
            }
        });
    }

    private void putProductById(RoutingContext routingContext) {
        Product product = null;
        try {
            product = jsonMapper.readValue(routingContext.getBodyAsString(), Product.class);
            product.setId(Integer.parseInt(routingContext.request().getParam("ID")));
        } catch (IOException e) {
            logger.error("Failed to convert payload to JSON", e);
        }
        DbMessage dbRequest = new DbMessage("updateProduct", product);

        eb.send("DbHandler", dbRequest, reply -> {
            if (reply.succeeded()) {
                DbMessage dbResponse = (DbMessage) reply.result().body();

                if (dbResponse.getSuccess()) {
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

    private void postProduct(RoutingContext routingContext) {
        Product product = null;
        try {
            product = jsonMapper.readValue(routingContext.getBodyAsString(), Product.class);
        } catch (IOException e) {
            logger.error("Failed to convert payload to JSON", e);
        }
        DbMessage dbRequest = new DbMessage("addProduct", product);

        eb.send("DbHandler", dbRequest, reply -> {
            if (reply.succeeded()) {
                DbMessage dbResponse = (DbMessage) reply.result().body();

                if (dbResponse.getSuccess()) {
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

    private void deleteProductById(RoutingContext routingContext) {
        Integer id = Integer.parseInt(routingContext.request().getParam("ID"));
        DbMessage dbRequest1 = new DbMessage("getProductById", id);

        eb.send("DbHandler", dbRequest1, reply1 -> {
            if (reply1.succeeded()) {
                DbMessage dbResponse1 = (DbMessage) reply1.result().body();

                if (dbResponse1.getSuccess()) {
                    Product product = (Product) dbResponse1.getResult();
                    DbMessage dbRequest2 = new DbMessage("deleteProduct", product);

                    eb.send("DbHandler", dbRequest2, reply2 -> {
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
                            .setStatusCode(404)
                            .end();
                }
            }
        });

    }

}
