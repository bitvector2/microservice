package org.bitvector.microservice_test;

import java.util.List;

public class DbMessage {
    private final String action;
    private final List params;
    private final List results;

    // Request signature
    DbMessage(String action, List params) {
        this.action = action;
        this.params = params;
        this.results = null;
    }

    // Response signature
    DbMessage(List results) {
        this.action = null;
        this.params = null;
        this.results = results;
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

    @Override
    public String toString() {
        return action + ":" + params + ":" + results;
    }
}
