package com.example.parts_shop_be.category;

import com.example.parts_shop_be.product.ProductDto;

import java.util.List;

public class CategoryDto {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
