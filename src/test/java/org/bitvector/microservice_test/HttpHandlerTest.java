package org.bitvector.microservice_test;

import com.jayway.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class HttpHandlerTest {
    static Connection conn;

    @BeforeClass
    public static void onStart() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://10.130.48.236:5432/microservice",
                    "microservice",
                    "microservice"
            );
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void onStop() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void emptyProducts() {
        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("TRUNCATE products");
            stmt.executeUpdate("ALTER SEQUENCE products_id_seq RESTART WITH 1");
            conn.commit();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String addProduct() {
        Statement stmt = null;
        UUID uuid = UUID.randomUUID();

        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO products (name) VALUES ('" + uuid.toString() + "')");
            conn.commit();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return uuid.toString();
    }

    @Before
    public void setUp() {
        RestAssured.port = 8080;
        RestAssured.baseURI = "http://127.0.0.1";
    }

    @Test(timeout = 3000)
    public void givenEmptyProductsWhenGetAllThenRespondWith200AndJsonContentType() {
        emptyProducts();
        when().get("/products").then().contentType("application/json").statusCode(200);
    }

    @Test(timeout = 3000)
    public void givenEmptyProductsWhenGetAllThenRespondWithEmplyJsonList() {
        emptyProducts();
        when().get("/products").then().body("$", Matchers.empty());
    }

    @Test(timeout = 3000)
    public void givenEmptyProductsWhenGetByIdThenRespondWith404() {
        emptyProducts();
        when().get("/products/0").then().statusCode(404);
    }

    @Test(timeout = 3000)
    public void givenAddProductWhenGetAllThenRespondWithJsonList() {
        emptyProducts();
        String name = addProduct();
        when().get("/products").then().body("name", hasItem(name));
    }

    @Test(timeout = 3000)
    public void givenAddProductWhenGetByIdThenRespondWith200AndJsonContentType() {
        emptyProducts();
        addProduct();
        when().get("/products/1").then().contentType("application/json").statusCode(200);
    }

    @Test(timeout = 3000)
    public void givenAddProductWhenGetByIdThenRespondWithJsonDict() {
        emptyProducts();
        String name = addProduct();
        when().get("/products/1").then().body("name", equalTo(name));
    }

    @Test(timeout = 3000)
    public void givenAddProductWhenPutThenRespondWith200() {
        emptyProducts();
        addProduct();
        String json = "{\"name\":\"" + UUID.randomUUID().toString() + "\"}";
        given().contentType("application/json").body(json).when().put("/products/1").then().statusCode(200);
    }

    @Test(timeout = 3000)
    public void givenAddProductWhenDeleteThenRespondWith200() {
        emptyProducts();
        addProduct();
        when().delete("/products/1").then().statusCode(200);
    }

    @Test(timeout = 3000)
    public void givenEmptyProductsWhenPostThenRespondWith200() {
        emptyProducts();
        String json = "{\"name\":\"" + UUID.randomUUID().toString() + "\"}";
        given().contentType("application/json").body(json).when().post("/products").then().statusCode(200);
    }

    @Test(timeout = 3000)
    public void givenEmptyProductsWhenDeleteThenRespondWith404() {
        emptyProducts();
        when().delete("/products/1").then().statusCode(404);
    }
}