package org.bitvector.microservice_test;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Product {
    private Logger logger;
    private Session session;

    public Product(Session s) {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.Product");
        session = s;

        /*
        CQLSH prerequisites:

        CREATE KEYSPACE IF NOT EXISTS test WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};
        CREATE TABLE IF NOT EXISTS test.product ( id text, name text, price double, weight float, PRIMARY KEY (id, name, price, weight) );
        INSERT INTO test.product (id, name, price, weight) VALUES ('one', 'La Petite Tonkinoise', 99.99, 1.0);
        INSERT INTO test.product (id, name, price, weight) VALUES ('two', 'Bye Bye Blackbird', 9.99, 2.0);
         */

    }

    public void handleListProducts(RoutingContext routingContext) {
        Select query = QueryBuilder
                .select()
                .all()
                .from("test", "product");

        ResultSetFuture future = session.executeAsync(query);
        ResultSet data = future.getUninterruptibly();

        JsonArray arr = null;
        try {
            arr = Utility.resultSet2JsonArray(data);
        } catch (Exception e) {
            logger.error("Utility.resultSet2JsonArray failed...", e);
        }

        if (arr != null) {
            routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
        }
    }

    public void handleGetProduct(RoutingContext routingContext) {
        String productID = routingContext.request().getParam("productID");

        Statement query = QueryBuilder
                .select()
                .all()
                .from("test", "product")
                .where(QueryBuilder.eq("id", productID));

        ResultSetFuture future = session.executeAsync(query);
        ResultSet data = future.getUninterruptibly();

        JsonArray products = null;
        try {
            products = Utility.resultSet2JsonArray(data);
        } catch (Exception e) {
            logger.error("Utility.resultSet2JsonArray failed...", e);
        }

        if (products != null) {
            routingContext.response().putHeader("content-type", "application/json").end(products.encodePrettily());
        }
    }

    public void handlePostProduct(RoutingContext routingContext) {
        // This is just an echo route for testing right now

        String body = routingContext.getBodyAsString();
        JsonArray products = new JsonArray(body);

        /*
        Statement query = QueryBuilder
                .insertInto("test", "product")
                .values(names, values);
        */

        routingContext.response().putHeader("content-type", "application/json").end(products.encodePrettily());
    }

}
