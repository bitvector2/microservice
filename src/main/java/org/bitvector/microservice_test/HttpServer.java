package org.bitvector.microservice_test;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpServer extends AbstractVerticle {
    private Logger logger;

    @Override
    public void start() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.HttpServer");

        // Start Controller
        ProductController productController = new ProductController(vertx.eventBus());

        // Start HTTP Router
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.head("/products").handler(productController::handleHeadProduct);
        router.get("/products").handler(productController::handleGetAllProduct);
        router.get("/products/:productID").handler(productController::handleGetProductId);
        router.put("/products/:productID").handler(productController::handlePutProductId);
        router.post("/products").handler(productController::handlePostProduct);
        router.delete("/products/:productID").handler(productController::handleDeleteProductId);

        // Start HTTP Listener
        vertx.createHttpServer().requestHandler(router::accept).listen(
                Integer.parseInt(System.getProperty("org.bitvector.microservice_test.listen-port")),
                System.getProperty("org.bitvector.microservice_test.listen-address")
        );

        logger.info("Started a HttpServer...");
    }

    @Override
    public void stop() {
        logger.info("Stopped a HttpServer...");
    }

}

