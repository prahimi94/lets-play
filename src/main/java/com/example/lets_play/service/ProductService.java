package com.example.lets_play.service;

import com.example.lets_play.model.Product;
import com.example.lets_play.repository.ProductRepository;
import com.example.lets_play.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product createProduct(String name, String desc, Double price, String userId) {
        Product p = new Product(name, desc, price, userId);
        return repository.save(p);
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public List<Product> searchProducts(String query) {
        return repository.findByNameContainingIgnoreCase(query);
    }

    public void deleteProduct(String productId) {
        repository.deleteById(productId);
    }

    public Product updateProduct(String productId, String name, String desc, Double price, String userId) {
        return repository.findById(productId).map(p -> {
            p.setName(name);
            p.setDescription(desc);
            p.setPrice(price);
            p.setUserId(userId);
            return repository.save(p);
        }).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
}