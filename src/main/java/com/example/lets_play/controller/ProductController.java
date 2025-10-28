package com.example.lets_play.controller;

import com.example.lets_play.model.Product;
import com.example.lets_play.service.ProductService;
import com.example.lets_play.service.UserService;
import com.example.lets_play.service.ValidationService;
import com.example.lets_play.model.User;
import com.example.lets_play.dto.ProductRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;
    
    @Autowired
    private ValidationService validationService;
    
    // Benefits of constructor injection:
    // -Dependencies are immutable (final)
    // -Better for testing (easy to mock)
    // -Ensures all required dependencies are provided
    // private final UserService userService;
    // private final ProductService productService;
    // public ProductController(UserService userService, ProductService productService) {
    //     this.userService = userService;
    //     this.productService = productService;
    // }

    @GetMapping
    public List<Product> getAll() {
        return productService.getAllProducts();
    }
    
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam(required = false) String q) {
        // Validate and sanitize search query
        String sanitizedQuery = validationService.validateAndSanitizeSearchQuery(q);
        
        if (sanitizedQuery.isEmpty()) {
            return productService.getAllProducts();
        }
        
        return productService.searchProducts(sanitizedQuery);
    }

    // Example of @PostAuthorize usage - checks authorization AFTER method execution
    // This allows access to the returned product to verify ownership
    @GetMapping("/{id}")
    @PostAuthorize("hasRole('ADMIN') or returnObject.userId == authentication.name")
    public Product getProductById(@PathVariable String id) {
        // Validate ObjectId format
        validationService.validateObjectId(id, "Product");
        
        // Method executes first, then @PostAuthorize checks if:
        // - User is ADMIN, OR
        // - The returned product belongs to the authenticated user
        return productService.getProductById(id);
    }

    // Another @PostAuthorize example with complex business logic
    // This could be used for products that have different visibility levels
    @GetMapping("/{id}/details")
    @PostAuthorize("hasRole('ADMIN') or (returnObject.userId == authentication.name) or returnObject.price < 100")
    public Product getProductDetails(@PathVariable String id) {
        validationService.validateObjectId(id, "Product");
        
        // Complex authorization logic:
        // - Admin can see all product details
        // - Product owner can see their product details
        // - Regular users can only see details of products under $100
        return productService.getProductById(id);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Product> createProduct(
            @Valid @RequestBody ProductRequest request,
            @RequestHeader("Authorization") String authHeader) {

        // Additional custom validation
        validationService.validateProductRequest(request);
        
        User user = userService.getUserFromToken(authHeader);
        Product product = productService.createProduct(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                user.getId()
        );

        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @productService.isProductOwner(#id, authentication.name)")
    public ResponseEntity<Product> update(@PathVariable String id,
            @Valid @RequestBody ProductRequest request,
            @RequestHeader("Authorization") String authHeader) {

        // Validate ObjectId format
        validationService.validateObjectId(id, "Product");
        
        // Additional custom validation
        validationService.validateProductRequest(request);

        User user = userService.getUserFromToken(authHeader);
        Product updatedProduct = productService.updateProduct(
                id,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                user.getId()
        );

        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @productService.isProductOwner(#id, authentication.name)")
    public void delete(@PathVariable String id) {
        // Validate ObjectId format
        validationService.validateObjectId(id, "Product");
        
        productService.deleteProduct(id);
    }
}
