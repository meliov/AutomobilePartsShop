package com.example.parts_shop_be.product;

import com.example.parts_shop_be.category.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;
    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository =categoryRepository;
    }

    public Page<Product> getProducts(int page, int limit, String search,
                                     Double minPrice, Double maxPrice,
                                     String sortBy, String sortOrder,
                                     String category) {
        Sort sort;

        if (sortBy.equals("price")) {
            // We'll handle sorting manually via criteria
            sort = Sort.unsorted();
        } else {
            sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        }

        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!search.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"));
            }

            if (!category.equals("all")) {
                Long categoryId = categoryRepository.findByName(category).getId();
                predicates.add(cb.equal(root.get("category"), categoryId));
            }

            predicates.add(cb.between(root.get("price"), minPrice, maxPrice));
            predicates.add(cb.greaterThanOrEqualTo(root.get("quantity"), 1));

            // 🔑 Handle sorting by "discountedPrice if present, else price"
            if (sortBy.equals("price")) {
                Expression<Number> priceExpr = cb.coalesce(root.get("discountedPrice"), root.get("price"));
                if (sortOrder.equalsIgnoreCase("asc")) {
                    query.orderBy(cb.asc(priceExpr));
                } else {
                    query.orderBy(cb.desc(priceExpr));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return productRepository.findAll(spec, pageable);
    }


    public Product getProduct(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
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
            existingProduct.setImage(updatedProduct.getImage());
            existingProduct.setTitle(updatedProduct.getTitle());
            existingProduct.setDiscount(updatedProduct.getDiscount());
            existingProduct.setDiscountedPrice(updatedProduct.getDiscountedPrice());
            existingProduct.setRating(updatedProduct.getRating());
            existingProduct.setQuantity(updatedProduct.getQuantity());
            existingProduct.setAdditionalImages(updatedProduct.getAdditionalImages());
            return productRepository.save(existingProduct);
        }
        return null;
    }
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }



}
