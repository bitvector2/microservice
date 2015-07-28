package org.bitvector.microservice_test;

public class DbMessage {
    private String action;
    private String params;
    private String result;

    // Request signature
    DbMessage(String action, String params) {
        this.action = action;
        this.params = params;
        this.result = null;
    }

    // Response signature
    DbMessage(String result) {
        this.action = null;
        this.params = null;
        this.result = result;
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
        return action + ":" + params + ":" + result;
    }
}
