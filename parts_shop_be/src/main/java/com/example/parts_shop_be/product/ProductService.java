package com.example.parts_shop_be.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getCategories() {
        return productRepository.findAll();  // Assuming product categories are distinct
    }

    public List<Product> getProductsByCategory(String category, int page, int limit) {
        // Use pagination, limit, and offset logic here
        // Example of getting products by category
        return productRepository.findByCategory(category);
    }

    public List<Product> getProducts(int page, int limit, String search, Double minPrice, Double maxPrice, String sortBy, String sortOrder, String category) {
        // Apply filters and sorting
        List<Product> products;
        if (!category.equals("all")) {
            products = productRepository.findByCategory(category);
        } else {
            products = productRepository.findByNameContainingIgnoreCase(search);
        }

        return products;
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct != null) {
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setDescription(updatedProduct.getDescription());
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
