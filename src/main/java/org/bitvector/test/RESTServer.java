package org.bitvector.test;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class RESTServer extends AbstractVerticle {
    private Product productColl;

    @Override
    public void start() {
        productColl = new Product();

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.get("/products/:productID").handler(productColl::handleGetProduct);
        router.put("/products/:productID").handler(productColl::handleAddProduct);
        router.get("/products").handler(productColl::handleListProducts);

        vertx.createHttpServer().requestHandler(router::accept).listen(
                Integer.parseInt(System.getProperty("org.bitvector.test.listen-port")),
                System.getProperty("org.bitvector.test.listen-address")
        );
    }

    @Override
    public void stop() {
        // FIXME
    }

}

