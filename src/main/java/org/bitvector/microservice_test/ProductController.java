package org.bitvector.microservice_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProductController {
    EventBus eb;
    private Logger logger;
    private ObjectMapper jsonMapper;

    public ProductController(EventBus eb) {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.ProductController");
        this.eb = eb;

        DbMessageCodec dbMessageCodec = new DbMessageCodec();
        eb.registerDefaultCodec(DbMessage.class, dbMessageCodec);

        eb.send("DbPersister", new DbMessage("ping", null), reply -> {
            if (reply.succeeded()) {
                DbMessage dbMessage = (DbMessage) reply.result().body();
                logger.info("Received: " + dbMessage.toString());
            }
        });

        jsonMapper = new ObjectMapper();
    }

    public void handleHeadProduct(RoutingContext routingContext) {
        // Route used for benchmarking without database lookup
        Product obj = new Product("asdf", "asdf", 1.0, (float) 1.0);

        String product = null;
        try {
            product = jsonMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("Failed to map a Product to JSON.", e);
        }

        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(product);
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
