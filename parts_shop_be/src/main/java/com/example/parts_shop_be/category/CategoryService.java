package com.example.parts_shop_be.category;

import com.example.parts_shop_be.product.Product;
import com.example.parts_shop_be.product.ProductDto;
import com.example.parts_shop_be.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();  // Assuming product categories are distinct
    }


    public Page<Product> getProductsByCategory(String category, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Category categoryObj = categoryRepository.findByName(category);
        return productRepository.findByCategory(categoryObj, pageable);
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category category) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        existingCategory.setName(category.getName());
        existingCategory.setNameFr(category.getNameFr());
        existingCategory.setNameBg(category.getNameBg());
        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
