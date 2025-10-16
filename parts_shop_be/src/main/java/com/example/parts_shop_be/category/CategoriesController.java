package com.example.parts_shop_be.category;

import com.example.parts_shop_be.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/categories")
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/get")
    public ResponseEntity<Set<String>> getCategories() {
        return new ResponseEntity<>(categoryService.getCategories(), HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Category>> getAllCategories() {
        return  ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        Page<Product> productPage = categoryService.getProductsByCategory(category, page, limit);

        if (productPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "error", Map.of(
                                    "message", "Failed to fetch products in category",
                                    "code", "CATEGORY_PRODUCTS_FETCH_ERROR"
                            )
                    ));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", productPage.getContent());
        response.put("meta", Map.of(
                "total", productPage.getTotalElements(),
                "page", page,
                "limit", limit
        ));

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
            return ResponseEntity.ok(categoryService.createCategory(category));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
            Category updatedCategory = categoryService.updateCategory(id, category);
            return ResponseEntity.ok(updatedCategory);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteCategory(@PathVariable Long id) {
           categoryService.deleteCategory(id);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Category deleted successfully"
            ));

    }
}
