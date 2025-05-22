package com.example.parts_shop_be.category;

import com.example.parts_shop_be.product.Product;
import com.example.parts_shop_be.product.ProductDto;
import com.example.parts_shop_be.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;


    @Autowired
    private ProductRepository productRepository;
    public Set<String> getCategories() {
        return categoryRepository.findAll().stream().map(Category::getName).collect(Collectors.toSet());  // Assuming product categories are distinct
    }

    public Page<Product> getProductsByCategory(String category, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Category categoryObj = categoryRepository.findByName(category);
        return productRepository.findByCategory(categoryObj, pageable);
    }
}
