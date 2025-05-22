package com.example.parts_shop_be.category;

import com.example.parts_shop_be.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
}
