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
    private MappingManager manager;

    private ProductAccessor productAccessor;
    private Mapper<Product> productMapper;

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

        EventBus eb = vertx.eventBus();
        eb.consumer("DbPersister", this::onMessage);
        DbMessageCodec dbMessageCodec = new DbMessageCodec();
        eb.registerDefaultCodec(DbMessage.class, dbMessageCodec);

        productAccessor = manager.createAccessor(ProductAccessor.class);
        productMapper = manager.mapper(Product.class);

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
            case "ping":
                this.handlePing(message);
        }
    }

    private void handlePing(Message<DbMessage> message) {
        DbMessage dbResponse = new DbMessage("pong");
        message.reply(dbResponse);
    }
}
