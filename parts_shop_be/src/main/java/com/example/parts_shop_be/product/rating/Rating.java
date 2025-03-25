package com.example.parts_shop_be.product.rating;


import com.example.parts_shop_be.utils.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Rating extends BaseEntity {
    @Column(nullable = false)
    private Double average;

    @Column(nullable = false)
    private Integer count;

    public Rating() {}

    public Rating(Double average, Integer count) {
        this.average = average;
        this.count = count;
    }

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}