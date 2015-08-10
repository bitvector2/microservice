package org.bitvector.microservice;

import java.util.List;

public class DbMessage {
    private String action = null;
    private Object param = null;
    private Object result = null;
    private Boolean success = null;

    // Product Request signatures
    DbMessage(String action) {
        this.action = action;
    }

    DbMessage(String action, Integer param) {
        this.action = action;
        this.param = param;
    }

    DbMessage(String action, Product param) {
        this.action = action;
        this.param = param;
    }

    // Product Response signatures
    DbMessage(Boolean success) {
        this.success = success;
    }

    DbMessage(Boolean success, Product result) {
        this.success = success;
        this.result = result;
    }

    DbMessage(Boolean success, List<Product> result) {
        this.success = success;
        this.result = result;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Object getResult() {
        return result;
    }

    public String getAction() {
        return action;
    }

    public Object getParam() {
        return param;
    }
}
