package org.bitvector.microservice_test;

import java.util.List;


public final class DbMessage {
    private final String action;
    private final List params;
    private final List results;
    private final Boolean success;

    // Request constructor
    DbMessage(String action, List params) {
        assert (action != null);
        this.action = action;
        this.params = params;
        this.results = null;
        this.success = null;
    }

    // Response constructor
    DbMessage(Boolean succeeded, List results) {
        assert (succeeded != null);
        this.action = null;
        this.params = null;
        this.results = results;
        this.success = succeeded;
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
        return success;
    }

}
