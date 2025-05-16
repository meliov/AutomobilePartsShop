package com.example.parts_shop_be.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Set<String> getCategories() {
        return productRepository.findAll().stream().map(it -> it.getCategory()).collect(Collectors.toSet());  // Assuming product categories are distinct
    }

    public Page<Product> getProductsByCategory(String category, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        return productRepository.findByCategory(category, pageable);
    }

    public Page<Product> getProducts(int page, int limit, String search, Double minPrice, Double maxPrice, String sortBy, String sortOrder, String category) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!search.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"));
            }

            if (!category.equals("all")) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            predicates.add(cb.between(root.get("price"), minPrice, maxPrice));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return productRepository.findAll(spec, pageable);
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
