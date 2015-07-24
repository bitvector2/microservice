package org.bitvector.microservice_test;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProductCtrl {
    private Logger logger;
    private Session session;
    private Mapper<Product> mapper;

    public ProductCtrl(Session s) {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.ProductCtrl");
        session = s;
        mapper = new MappingManager(session).mapper(Product.class);
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
        // This is just an echo route

        String body = routingContext.getBodyAsString();
        JsonArray products = new JsonArray(body);

        routingContext.response().putHeader("content-type", "application/json").end(products.encodePrettily());
    }

}
