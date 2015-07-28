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
        router.head("/products").handler(this::handleHeadProduct);
        router.get("/products").handler(this::handleGetAllProduct);
        router.get("/products/:productID").handler(this::handleGetProductId);
        router.put("/products/:productID").handler(this::handlePutProductId);
        router.post("/products").handler(this::handlePostProduct);
        router.delete("/products/:productID").handler(this::handleDeleteProductId);

        // Start HTTP Listener
        vertx.createHttpServer().requestHandler(router::accept).listen(
                Integer.parseInt(System.getProperty("org.bitvector.microservice_test.listen-port")),
                System.getProperty("org.bitvector.microservice_test.listen-address")
        );

        logger.info("Started a HttpRouter...");
    }

    @Override
    public void stop() {
        logger.info("Stopped a HttpRouter...");
    }

    public void handleHeadProduct(RoutingContext routingContext) {

        DbMessage dbRequest = new DbMessage("ping", null);
        logger.info("Sending: " + dbRequest.toString());
        eb.send("DbPersister", dbRequest, reply -> {
            if (reply.succeeded()) {
                DbMessage dbResponse = (DbMessage) reply.result().body();
                logger.info("Received: " + dbResponse.toString());
            }
        });

        routingContext.response()
                .setStatusCode(200)
                .end();

    }

    public void handleGetAllProduct(RoutingContext routingContext) {
        /*
        ListenableFuture<Result<Product>> future = productAccessor.getAllAsync();

        Result<Product> objs = null;
        try {
            objs = future.get();
        } catch (Exception e) {
            logger.error("Failed to get Products from DB.", e);
        }

        String products = null;
        try {
            if (objs != null) {
                products = jsonMapper.writeValueAsString(objs.all());
            }
        } catch (Exception e) {
            logger.error("Failed to map Products to JSON.", e);
        }

        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(products);
        */
    }

    public void handleGetProductId(RoutingContext routingContext) {
        /*
        ListenableFuture<Product> future = productMapper.getAsync(routingContext.request().getParam("productID"));

        Product obj = null;
        try {
            obj = future.get();
        } catch (Exception e) {
            logger.error("Failed to get a Product from DB.", e);
        }

        String product = null;
        try {
            product = jsonMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("Failed to map a Product to JSON.", e);
        }

        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(product);
        */
    }

    public void handlePutProductId(RoutingContext routingContext) {
        /*
        ListenableFuture<Product> future = productMapper.getAsync(routingContext.request().getParam("productID"));
        String body = routingContext.getBodyAsString();

        Product obj = null;
        try {
            obj = future.get();
        } catch (Exception e) {
            logger.error("Failed to get a Product from DB before delete", e);
        }

        productMapper.deleteAsync(obj);

        Product product = null;
        try {
            product = jsonMapper.readValue(body, Product.class);
        } catch (Exception e) {
            logger.error("Failed to map JSON to a Product");
        }

        productMapper.saveAsync(product);

        routingContext.response()
                .setStatusCode(202)
                .end();
        */
    }

    public void handlePostProduct(RoutingContext routingContext) {
        /*
        String body = routingContext.getBodyAsString();

        Product product = null;
        try {
            product = jsonMapper.readValue(body, Product.class);
        } catch (Exception e) {
            logger.error("Failed to map JSON to a Product");
        }

        productMapper.saveAsync(product);

        routingContext.response()
                .setStatusCode(202)
                .end();
        */
    }

    public void handleDeleteProductId(RoutingContext routingContext) {
        /*
        ListenableFuture<Product> future = productMapper.getAsync(routingContext.request().getParam("productID"));

        Product obj = null;
        try {
            obj = future.get();
        } catch (Exception e) {
            logger.error("Failed to get a Product from DB before delete", e);
        }

        productMapper.deleteAsync(obj);

        routingContext.response()
                .setStatusCode(202)
                .end();
        */
    }

}

