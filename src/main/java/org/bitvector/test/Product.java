package org.bitvector.test;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;

public class Product {

    private AsyncSQLClient client;
    private final HashMap<String, JsonObject> products = new HashMap<>();

    public Product(Vertx vertx) {

        addProduct(new JsonObject().put("id", "prod3568").put("name", "Egg Whisk").put("price", 3.99).put("weight", 150));
        addProduct(new JsonObject().put("id", "prod7340").put("name", "Tea Cosy").put("price", 5.99).put("weight", 100));
        addProduct(new JsonObject().put("id", "prod8643").put("name", "Spatula").put("price", 1.00).put("weight", 80));

        JsonObject config = new JsonObject()
                .put("host", System.getProperty("org.bitvector.test.sql-host"))
                .put("port", System.getProperty("org.bitvector.test.sql-port"))
                .put("maxPoolSize", System.getProperty("org.bitvector.test.sql-maxpoolsize"))
                .put("username", System.getProperty("org.bitvector.test.sql-username"))
                .put("password", System.getProperty("org.bitvector.test.sql-password"))
                .put("database", System.getProperty("org.bitvector.test.sql-database"));

        // client = PostgreSQLClient.createShared(vertx, config);

    }

    private void addProduct(JsonObject product) {
/*        client.getConnection(res -> {
            if (res.succeeded()) {

                SQLConnection connection = res.result();

                // Got a connection

            } else {
                // Failed to get connection - deal with it
            }
        });*/

        products.put(product.getString("id"), product);
    }

    public void handleGetProduct(RoutingContext routingContext) {
        String productID = routingContext.request().getParam("productID");
        HttpServerResponse response = routingContext.response();
        if (productID == null) {
            sendError(400, response);
        } else {
            JsonObject product = products.get(productID);
            if (product == null) {
                sendError(404, response);
            } else {
                response.putHeader("content-type", "application/json").end(product.encodePrettily());
            }
        }
    }

    public void handleAddProduct(RoutingContext routingContext) {
        String productID = routingContext.request().getParam("productID");
        HttpServerResponse response = routingContext.response();
        if (productID == null) {
            sendError(400, response);
        } else {
            JsonObject product = routingContext.getBodyAsJson();
            if (product == null) {
                sendError(400, response);
            } else {
                products.put(productID, product);
                response.end();
            }
        }
    }

    public void handleListProducts(RoutingContext routingContext) {
        JsonArray arr = new JsonArray();
        products.forEach((k, v) -> arr.add(v));
        routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

}
