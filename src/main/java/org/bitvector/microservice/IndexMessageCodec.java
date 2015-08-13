package org.bitvector.microservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class IndexMessageCodec implements MessageCodec<IndexMessage, IndexMessage> {

    private Logger logger;
    private ObjectMapper jsonMapper;

    IndexMessageCodec() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice.DbMessageCodec");
        jsonMapper = new ObjectMapper();
    }

    @Override
    public void encodeToWire(Buffer buffer, IndexMessage indexMessage) {
        try {
            buffer.appendBytes(jsonMapper.writeValueAsBytes(indexMessage));
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert message to wire format", e);
        }
    }

    @Override
    public IndexMessage decodeFromWire(int pos, Buffer buffer) {
        IndexMessage indexMessage = null;
        try {
            indexMessage = jsonMapper.readValue(buffer.getBytes(), IndexMessage.class);
        } catch (IOException e) {
            logger.error("Failed to convert message from wire format", e);
        }
        return indexMessage;
    }

    @Override
    public IndexMessage transform(IndexMessage indexMessage) {
        return indexMessage;
    }

    @Override
    public String name() {
        return "IndexMessageCodec";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }

}
