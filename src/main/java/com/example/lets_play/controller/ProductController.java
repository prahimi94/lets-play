package com.example.lets_play.controller;

import com.example.lets_play.model.Product;
import com.example.lets_play.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    @PostMapping
    public Product create(@RequestBody Product p) {
        return productService.createProduct(p.getName(), p.getDescription(), p.getPrice(), p.getUserId());
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable String id, @RequestBody Product p) {
        return productService.updateProduct(id, p.getName(), p.getDescription(), p.getPrice());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        productService.deleteProduct(id);
    }
}
