package org.bitvector.microservice_test;

import java.util.UUID;

public class DbMessage {
    private UUID uuid;
    private String action;
    private String params;
    private String result;

    // Request signature
    DbMessage(String action, String params) {
        this.uuid = UUID.randomUUID();
        this.action = action;
        this.params = params;
        this.result = null;
    }

    // Response signature
    DbMessage(UUID uuid, String result) {
        this.uuid = uuid;
        this.action = null;
        this.params = null;
        this.result = result;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return uuid.toString() + ":" + action + ":" + params + ":" + result;
    }
}
