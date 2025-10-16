package com.example.parts_shop_be.category;

import com.example.parts_shop_be.utils.BaseEntity;

import javax.persistence.Entity;

@Entity
public class Category extends BaseEntity {
    private String name;
    private String nameBg;
    private String nameFr;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameBg() {
        return nameBg;
    }

    public void setNameBg(String nameBg) {
        this.nameBg = nameBg;
    }

    public String getNameFr() {
        return nameFr;
    }

    public void setNameFr(String nameFr) {
        this.nameFr = nameFr;
    }
}
