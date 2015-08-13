package org.bitvector.microservice;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexHandler extends AbstractVerticle {
    private Logger logger;

    @Override
    public void start() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice.IndexHandler");

        EventBus eb = vertx.eventBus();
        eb.consumer("IndexHandler", this::onMessage);
        IndexMessageCodec indexMessageCodec = new IndexMessageCodec();
        eb.registerDefaultCodec(IndexMessage.class, indexMessageCodec);

        logger.info("Started a IndexHandler...");
    }

    @Override
    public void stop() {
        logger.info("Stopped a IndexHandler...");
    }

    private void onMessage(Message<IndexMessage> message) {
        switch (message.body().getAction()) {
            case "test": {
                message.reply(new IndexMessage(true));
            }
            break;
            default: {
                logger.error("Received message with an unknown action.");
                message.reply(new IndexMessage(false));
            }
            break;
        }
    }

}
