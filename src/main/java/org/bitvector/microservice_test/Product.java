package org.bitvector.microservice_test;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.Objects;


@Table(keyspace = "microservice_test", name = "products")
public class Product {

    /*
    CQLSH prerequisites:

    CREATE KEYSPACE IF NOT EXISTS microservice_test WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};
    CREATE TABLE IF NOT EXISTS microservice_test.products ( id text, name text, price double, weight float, PRIMARY KEY (id) );
    INSERT INTO microservice_test.products (id, name, price, weight) VALUES ('one', 'La Petite Tonkinoise', 99.99, 1.0);
    INSERT INTO microservice_test.products (id, name, price, weight) VALUES ('two', 'Bye Bye Blackbird', 9.99, 2.0);
     */

    @PartitionKey(value = 0)
    private String id;
    private String name;
    private Double price;
    private Float weight;

    public Product() {
    }

    public Product(String id, String name, Double price, Float weight) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Product) {
            Product that = (Product) other;
            return Objects.equals(this.id, that.id) &&
                    Objects.equals(this.name, that.name) &&
                    Objects.equals(this.price, that.price) &&
                    Objects.equals(this.weight, that.weight);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id) +
                Objects.hashCode(this.name) +
                Objects.hashCode(this.price) +
                Objects.hashCode(this.weight);
    }

}

