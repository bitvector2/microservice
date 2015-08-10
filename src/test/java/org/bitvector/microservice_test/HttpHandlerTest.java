package org.bitvector.microservice_test;

import com.jayway.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jayway.restassured.RestAssured.when;

public class HttpHandlerTest {
    Logger logger = LoggerFactory.getLogger("org.bitvector.microservice.HttpHandlerTest");

    @Before
    public void setUp() {
        RestAssured.port = 8080;
        RestAssured.baseURI = "http://127.0.0.1";
    }

    @Test(timeout = 3000)
    public void givenEmptyProductsWhenGetAllThenRespondWith200AndJsonContentType() {
        when().get("/products").then().contentType("application/json").statusCode(200);
    }

    @Test(timeout = 3000)
    public void givenEmptyProductsWhenGetAllThenRespondWithEmplyJsonList() {
        when().get("/products").then().body("$", Matchers.empty());
    }

    @Test(timeout = 3000)
    public void givenEmptyProductsWhenGetByIdThenRespondWith404() {
        when().get("/products/0").then().statusCode(404);
    }

    @Test(timeout = 3000)
    public void givenSomeProductsWhenGetAllThenRespondWith200AndJsonContentType() {
        when().get("/products").then().contentType("application/json").statusCode(200);
    }
}