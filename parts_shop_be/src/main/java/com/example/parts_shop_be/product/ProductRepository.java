package com.example.parts_shop_be.product;

import com.example.parts_shop_be.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Page<Product> findByCategory(Category category, Pageable pageable);
    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

}