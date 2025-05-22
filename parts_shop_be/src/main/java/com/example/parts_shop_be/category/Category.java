package com.example.parts_shop_be.category;

import com.example.parts_shop_be.product.Product;
import com.example.parts_shop_be.utils.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Category extends BaseEntity {
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
