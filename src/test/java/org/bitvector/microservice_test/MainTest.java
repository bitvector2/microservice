package org.bitvector.microservice_test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class MainTest {
    Logger logger = LoggerFactory.getLogger("org.bitvector.microservice.MainTest");

    @Test
    public void evaluatesExpression() {
        assertEquals(6, 6);
        logger.info("A Test Ran");
    }
}