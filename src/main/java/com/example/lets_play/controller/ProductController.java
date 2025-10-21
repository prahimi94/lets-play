package com.example.lets_play.controller;

import com.example.lets_play.model.Product;
import com.example.lets_play.service.ProductService;
import com.example.lets_play.service.UserService;
import com.example.lets_play.model.User;
import com.example.lets_play.dto.ProductRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;
    
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

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody ProductRequest request,
            @RequestHeader("Authorization") String authHeader) {

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
    public ResponseEntity<Product> update(@PathVariable String id, 
            @RequestBody ProductRequest request,
            @RequestHeader("Authorization") String authHeader) {

        User user = userService.getUserFromToken(authHeader);
        Product product = productService.updateProduct(
                id,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                user.getId()
        );

        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        productService.deleteProduct(id);
    }
}
