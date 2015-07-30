package org.bitvector.microservice_test;

import java.util.List;

public class DbMessage {
    private final String action;
    private final List params;
    private final List results;
    private final Boolean succeeded;

    // Request signature
    DbMessage(String action, List params) {
        this.action = action;
        this.params = params;
        this.results = null;
        this.succeeded = null;
    }

    // Response signature
    DbMessage(Boolean succeeded, List results) {
        this.action = null;
        this.params = null;
        this.results = results;
        this.succeeded = succeeded;
    }

    public String getAction() {
        return action;
    }

    public List getParams() {
        return params;
    }

    public List getResults() {
        return results;
    }

    public Boolean succeeded() {
        return succeeded;
    }

}
