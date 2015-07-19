package org.bitvector.test;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class RESTServer extends AbstractVerticle {

    @Override
    public void start() {
        Product myStuff = new Product(vertx);

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.get("/products/:productID").handler(myStuff::handleGetProduct);
        router.put("/products/:productID").handler(myStuff::handleAddProduct);
        router.get("/products").handler(myStuff::handleListProducts);

        vertx.createHttpServer().requestHandler(router::accept).listen(
                Integer.parseInt(System.getProperty("org.bitvector.test.listen-port")),
                System.getProperty("org.bitvector.test.listen-address")
        );
    }

    @Override
    public void stop() {
        vertx.close();
    }

}

