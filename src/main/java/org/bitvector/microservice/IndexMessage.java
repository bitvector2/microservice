package org.bitvector.microservice;

import java.util.List;

public class IndexMessage {
    private String action = null;
    private Object param = null;
    private Object result = null;
    private Boolean success = null;

    // Generic signatures
    IndexMessage(String action) {
        this.action = action;
    }

    IndexMessage(String action, Integer param) {
        this.action = action;
        this.param = param;
    }

    IndexMessage(Boolean success) {
        this.success = success;
    }

    // Product specific signatures
    IndexMessage(String action, Product param) {
        this.action = action;
        this.param = param;
    }

    IndexMessage(Boolean success, Product result) {
        this.success = success;
        this.result = result;
    }

    IndexMessage(Boolean success, List<Product> result) {
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
