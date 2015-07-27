package org.bitvector.microservice_test;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbPersister extends AbstractVerticle {

    private Logger logger;

    @Override
    public void start() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.DbPersister");

        EventBus eb = vertx.eventBus();
        eb.consumer("DbPersister", this::onMessage);
        DbMessageCodec dbMessageCodec = new DbMessageCodec();
        eb.registerDefaultCodec(DbMessage.class, dbMessageCodec);

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
