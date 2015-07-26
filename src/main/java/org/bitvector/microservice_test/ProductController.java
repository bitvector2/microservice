package org.bitvector.microservice_test;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.google.common.util.concurrent.ListenableFuture;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProductController {
    private Logger logger;
    private ProductAccessor productAccessor;
    private Mapper<Product> productMapper;

    public ProductController(Session s) {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.ProductController");
        MappingManager manager = new MappingManager(s);
        productAccessor = manager.createAccessor(ProductAccessor.class);
        productMapper = manager.mapper(Product.class);
    }

    public void handleListProducts(RoutingContext routingContext) {
        ListenableFuture<Result<Product>> future = productAccessor.getAllAsync();

        Result<Product> objs = null;
        try {
            objs = future.get();
        } catch (Exception e) {
            logger.error("Failed to get Products from DB.", e);
        }

        JsonArray products = new JsonArray();
        if (objs != null) {
            for (Product obj : objs.all()) {
                JsonObject jsonObj = new JsonObject(obj.toJson());
                products.add(jsonObj);
            }
        }

        routingContext.response().putHeader("content-type", "application/json").end(products.encodePrettily());
    }

    public void handleGetProduct(RoutingContext routingContext) {
        ListenableFuture<Product> future = productMapper.getAsync(routingContext.request().getParam("productID"));

        Product obj = null;
        try {
            obj = future.get();
        } catch (Exception e) {
            logger.error("Failed to get a Product from DB.", e);
        }

        JsonArray products = new JsonArray();
        if (obj != null) {
            JsonObject jsonObj = new JsonObject(obj.toJson());
            products.add(jsonObj);
        }

        routingContext.response().putHeader("content-type", "application/json").end(products.encodePrettily());
    }

    public void handlePostProduct(RoutingContext routingContext) {
        // This is just an echo route

        String body = routingContext.getBodyAsString();
        JsonArray products = new JsonArray(body);

        routingContext.response().putHeader("content-type", "application/json").end(products.encodePrettily());
    }

}
