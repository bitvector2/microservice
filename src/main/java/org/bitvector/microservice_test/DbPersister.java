package org.bitvector.microservice_test;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.ConstantReconnectionPolicy;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbPersister extends AbstractVerticle {

    private Logger logger;
    private Cluster cluster;
    private Session session;
    private MappingManager manager;
    private ObjectMapper jsonMapper;

    private ProductAccessor productAccessor; // Collection specific
    private Mapper<Product> productMapper; // Collection specific

    @Override
    public void start() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.DbPersister");

        String[] nodes = System.getProperty("org.bitvector.microservice_test.db-nodes").split(",");
        cluster = Cluster.builder()
                .addContactPoints(nodes)
                .withRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE)
                .withReconnectionPolicy(new ConstantReconnectionPolicy(100L))
                .build();
        session = cluster.connect();
        manager = new MappingManager(session);
        jsonMapper = new ObjectMapper();

        EventBus eb = vertx.eventBus();
        eb.consumer("DbPersister", this::onMessage);
        DbMessageCodec dbMessageCodec = new DbMessageCodec();
        eb.registerDefaultCodec(DbMessage.class, dbMessageCodec);

        productAccessor = manager.createAccessor(ProductAccessor.class); // Collection specific
        productMapper = manager.mapper(Product.class); // Collection specific

        logger.info("Started a DbPersister...");
    }

    @Override
    public void stop() {
        session.closeAsync();
        cluster.closeAsync();
        logger.info("Stopped a DbPersister...");
    }

    private void onMessage(Message<DbMessage> message) {
        switch (message.body().getAction()) {
            case "handleGetAllProducts":
                this.handleGetAllProducts(message);
            case "handleGetProductId":
                this.handleGetProductId(message);
            default:
                logger.error("Received message with an unknown action: " + "\"" + message.body().getAction() + "\"");
        }
    }

    private void handleGetAllProducts(Message<DbMessage> message) {
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

        DbMessage dbResponse = new DbMessage(products);
        message.reply(dbResponse);
    }

    private void handleGetProductId(Message<DbMessage> message) {
        String productID = message.body().getParams();

        ListenableFuture<Product> future = productMapper.getAsync(productID);

        Product obj = null;
        try {
            obj = future.get();
        } catch (Exception e) {
            logger.error("Failed to get a Product from DB.", e);
        }

        String product = null;
        try {
            if (obj != null) {
                product = jsonMapper.writeValueAsString(obj);
            }
        } catch (Exception e) {
            logger.error("Failed to map a Product to JSON.", e);
        }

        DbMessage dbResponse = new DbMessage(product);
        message.reply(dbResponse);
    }

    private void handlePutProductId(Message<DbMessage> message) {
        // FIXME
    }

    private void handlePostProduct(Message<DbMessage> message) {
        // FIXME
    }

    private void handleDeleteProductId(Message<DbMessage> message) {
        // FIXME
    }
}
