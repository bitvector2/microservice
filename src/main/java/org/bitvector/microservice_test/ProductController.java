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
    private Mapper<ProductModel> productMapper;
    private ObjectMapper jsonMapper;

    public ProductController(Session s) {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.ProductController");
        MappingManager manager = new MappingManager(s);
        productAccessor = manager.createAccessor(ProductAccessor.class);
        productMapper = manager.mapper(ProductModel.class);
        jsonMapper = new ObjectMapper();
    }

    public void handleGetAllProduct(RoutingContext routingContext) {
        ListenableFuture<Result<ProductModel>> future = productAccessor.getAllAsync();

        Result<ProductModel> objs = null;
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
        ListenableFuture<ProductModel> future = productMapper.getAsync(routingContext.request().getParam("productID"));

        ProductModel obj = null;
        try {
            obj = future.get();
        } catch (Exception e) {
            logger.error("Failed to get a ProductModel from DB.", e);
        }

        String product = null;
        try {
            product = jsonMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("Failed to map a ProductModel to JSON.", e);
        }

        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(product);
    }

    public void handlePutProductId(RoutingContext routingContext) {
        ListenableFuture<ProductModel> future = productMapper.getAsync(routingContext.request().getParam("productID"));
        String body = routingContext.getBodyAsString();

        ProductModel obj = null;
        try {
            obj = future.get();
        } catch (Exception e) {
            logger.error("Failed to get a ProductModel from DB before delete", e);
        }

        productMapper.deleteAsync(obj);

        ProductModel productModel = null;
        try {
            productModel = jsonMapper.readValue(body, ProductModel.class);
        } catch (Exception e) {
            logger.error("Failed to map JSON to a ProductModel");
        }

        productMapper.saveAsync(productModel);

        routingContext.response()
                .setStatusCode(202)
                .end();
    }

    public void handlePostProduct(RoutingContext routingContext) {
        String body = routingContext.getBodyAsString();

        ProductModel productModel = null;
        try {
            productModel = jsonMapper.readValue(body, ProductModel.class);
        } catch (Exception e) {
            logger.error("Failed to map JSON to a ProductModel");
        }

        productMapper.saveAsync(productModel);

        routingContext.response()
                .setStatusCode(202)
                .end();
    }

    public void handleDeleteProductId(RoutingContext routingContext) {
        ListenableFuture<ProductModel> future = productMapper.getAsync(routingContext.request().getParam("productID"));

        ProductModel obj = null;
        try {
            obj = future.get();
        } catch (Exception e) {
            logger.error("Failed to get a ProductModel from DB before delete", e);
        }

        productMapper.deleteAsync(obj);

        routingContext.response()
                .setStatusCode(202)
                .end();
    }

}
