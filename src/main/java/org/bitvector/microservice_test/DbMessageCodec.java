package org.bitvector.microservice_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbMessageCodec implements MessageCodec<DbMessage, DbMessage> {

    private Logger logger;
    private ObjectMapper jsonMapper;

    public DbMessageCodec() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.DbMessageCodec");
        jsonMapper = new ObjectMapper();
    }

    @Override
    public void encodeToWire(Buffer buffer, DbMessage dbMessage) {
        byte[] bytes = new byte[0];
        try {
            bytes = jsonMapper.writeValueAsBytes(dbMessage);
        } catch (Exception e) {
            logger.error("Failed to convert message to wire format", e);
        }
        buffer.setBytes(0, bytes);
    }

    @Override
    public DbMessage decodeFromWire(int pos, Buffer buffer) {
        DbMessage dbMessage = null;
        try {
            dbMessage = jsonMapper.readValue(buffer.getBytes(), DbMessage.class);
        } catch (Exception e) {
            logger.error("Failed to convert message from wire format", e);
        }
        return dbMessage;
    }

    @Override
    public DbMessage transform(DbMessage dbMessage) {
        return dbMessage;
    }

    @Override
    public String name() {
        return "DbMessageCodec";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
