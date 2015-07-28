package org.bitvector.microservice_test;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.ConstantReconnectionPolicy;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbPersister extends AbstractVerticle {

    private Logger logger;
    private Cluster cluster;
    private Session session;

    private ProductAccessor productAccessor;
    private Mapper<Product> productMapper;

    @Override
    public void start() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.DbPersister");

        EventBus eb = vertx.eventBus();
        eb.consumer("DbPersister", this::onMessage);
        DbMessageCodec dbMessageCodec = new DbMessageCodec();
        eb.registerDefaultCodec(DbMessage.class, dbMessageCodec);

        vertx.executeBlocking(future -> {
            // Start Database
            String[] nodes = System.getProperty("org.bitvector.microservice_test.db-nodes").split(",");
            cluster = Cluster.builder()
                    .addContactPoints(nodes)
                    .withRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE)
                    .withReconnectionPolicy(new ConstantReconnectionPolicy(100L))
                    .build();
            session = cluster.connect();
            future.complete();
        }, result -> {
            if (result.succeeded()) {
                MappingManager manager = new MappingManager(session);
                productAccessor = manager.createAccessor(ProductAccessor.class);
                productMapper = manager.mapper(Product.class);
            }
        });

        logger.info("Started a DbPersister...");
    }

    @Override
    public void stop() {
        logger.info("Stopped a DbPersister...");
    }

    private void onMessage(Message<DbMessage> message) {
        DbMessage dbMessage = message.body();
        logger.info("Received: " + dbMessage.toString());
        message.reply(new DbMessage(dbMessage.getUuid(), "pong"));
    }
}
