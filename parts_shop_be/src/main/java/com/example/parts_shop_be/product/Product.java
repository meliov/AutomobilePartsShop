package com.example.parts_shop_be.product;


import com.example.parts_shop_be.category.Category;
import com.example.parts_shop_be.product.rating.Rating;
import com.example.parts_shop_be.utils.BaseEntity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "products")
public class Product extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    private String image;
    private String title;
    private Boolean discount;
    private Double discountedPrice;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url", length = 1000)
    private List<String> additionalImages;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rating_id", referencedColumnName = "id")
    private Rating rating;

    public Product() {}

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getDiscount() {
        return discount;
    }

    public void setDiscount(Boolean discount) {
        this.discount = discount;
    }

    public Double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(Double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public List<String> getAdditionalImages() {
        return additionalImages;
    }

    public void setAdditionalImages(List<String> additionalImages) {
        this.additionalImages = additionalImages;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    // Getters and Setters
}

