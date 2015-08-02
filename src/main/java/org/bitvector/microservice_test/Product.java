package org.bitvector.microservice_test;

import javax.persistence.*;
import java.io.Serializable;


@Entity()
@Table(name = "products")
public class Product implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    Product() {}

    Product(Integer id) {
        this.id = id;
    }

    Product(String name) {
        this.name = name;
    }

    Product(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object other) {
        Product that;

        if (this == other) {
            return true;
        } else if (!(other instanceof Product)) {
            return false;
        } else {
            that = (Product) other;
        }

        return this.getId().equals(that.getId()) && this.getName().equals(that.getName());
    }
    
    public int hashCode() {
        return id.hashCode() + name.hashCode();
    }
    
}
