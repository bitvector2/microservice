package org.bitvector.microservice_test;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProductController {
    private Logger logger;
    private ProductAccessor productAccessor;
    private Mapper<Product> productMapper;
    private ObjectMapper jsonMapper;

    public ProductController(Session s) {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.ProductController");
        MappingManager manager = new MappingManager(s);
        productAccessor = manager.createAccessor(ProductAccessor.class);
        productMapper = manager.mapper(Product.class);
        jsonMapper = new ObjectMapper();
    }

    public void handleGetAllProduct(RoutingContext routingContext) {
        ListenableFuture<Result<Product>> future = productAccessor.getAllAsync();

        Result<Product> objs = null;
        try {
            objs = future.get();
        } catch (Exception e) {
            logger.error("Failed to get Products from DB.", e);
        }

        String products = null;
        try {
            products = jsonMapper.writeValueAsString(objs.all());
        } catch (Exception e) {
            logger.error("Failed to map Products to JSON.", e);
        }

        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(products);
    }

    public void handleGetProductId(RoutingContext routingContext) {
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
    }

    public void handlePutProductId(RoutingContext routingContext) {
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
    }

    public void handlePostProduct(RoutingContext routingContext) {
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
    }

    public void handleDeleteProductId(RoutingContext routingContext) {
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
    }

}
