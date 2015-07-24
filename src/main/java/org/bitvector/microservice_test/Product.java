package org.bitvector.microservice_test;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.google.common.base.Objects;

@Table(keyspace = "test", name = "product")
public class Product {

    /*
    CQLSH prerequisites:

    CREATE KEYSPACE IF NOT EXISTS test WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};
    CREATE TABLE IF NOT EXISTS test.product ( id text, name text, price double, weight float, PRIMARY KEY (id, name, price, weight) );
    INSERT INTO test.product (id, name, price, weight) VALUES ('one', 'La Petite Tonkinoise', 99.99, 1.0);
    INSERT INTO test.product (id, name, price, weight) VALUES ('two', 'Bye Bye Blackbird', 9.99, 2.0);
     */

    @PartitionKey
    private String id;
    @PartitionKey
    private String name;
    @PartitionKey
    private Double price;
    @PartitionKey
    private Float weight;

    public Product() {
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
            return Objects.equal(this.id, that.id) &&
                    Objects.equal(this.name, that.name) &&
                    Objects.equal(this.price, that.price) &&
                    Objects.equal(this.weight, that.weight);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, price, weight);
    }
}
