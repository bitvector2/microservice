package org.bitvector.microservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexHandler extends AbstractVerticle {
    private Logger logger;
    private Client client;
    private ObjectMapper mapper;

    @Override
    public void start() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice.IndexHandler");

        EventBus eb = vertx.eventBus();
        eb.consumer("IndexHandler", this::onMessage);
        IndexMessageCodec indexMessageCodec = new IndexMessageCodec();
        eb.registerDefaultCodec(IndexMessage.class, indexMessageCodec);

        InetSocketTransportAddress host = new InetSocketTransportAddress(
                System.getProperty("bitvector.microservice.index-address"),
                Integer.parseInt(System.getProperty("bitvector.microservice.index-port")));
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.sniff", true).build();
        TransportClient client = new TransportClient(settings)
                .addTransportAddress(host);

        mapper = new ObjectMapper();

        logger.info("Started a IndexHandler...");
    }

    @Override
    public void stop() {
        client.close();
        logger.info("Stopped a IndexHandler...");
    }

    private void onMessage(Message<IndexMessage> message) {
        switch (message.body().getAction()) {
            case "test": {
                message.reply(new IndexMessage(true));
                break;
            }
            default: {
                logger.error("Received message with an unknown action.");
                message.reply(new IndexMessage(false));
                break;
            }
        }
    }


}
