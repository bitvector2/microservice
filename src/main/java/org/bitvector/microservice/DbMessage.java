package org.bitvector.microservice;

import java.util.List;

public class DbMessage {
    private String action = null;
    private Object param = null;
    private Object result = null;
    private Boolean success = null;

    // Generic signatures
    DbMessage(String action) {
        this.action = action;
    }

    DbMessage(String action, Integer param) {
        this.action = action;
        this.param = param;
    }

    DbMessage(Boolean success) {
        this.success = success;
    }

    // Product specific signatures
    DbMessage(String action, Product param) {
        this.action = action;
        this.param = param;
    }

    DbMessage(Boolean success, Product result) {
        this.success = success;
        this.result = result;
    }

    DbMessage(Boolean success, List<Product> result) {
        this.success = success;
        this.result = result;
    }

    // Future entity specific signatures go here

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
