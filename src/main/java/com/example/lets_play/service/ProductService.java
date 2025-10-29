package com.example.lets_play.service;

import com.example.lets_play.model.Product;
import com.example.lets_play.model.User;
import com.example.lets_play.repository.ProductRepository;
import com.example.lets_play.repository.UserRepository;
import com.example.lets_play.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    public Product getProductById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public Product createProduct(String name, String desc, Double price, String userId) {
        Product p = new Product(name, desc, price, userId);
        return productRepository.save(p);
    }

    public void deleteProduct(String productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.deleteById(productId);
    }

    public Product updateProduct(String productId, String name, String desc, Double price, String userId) {
        return productRepository.findById(productId).map(p -> {
            p.setName(name);
            p.setDescription(desc);
            p.setPrice(price);
            p.setUserId(userId);
            return productRepository.save(p);
        }).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
    
    public boolean isProductOwner(String productId, String userEmail) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent()) {
            return false;
        }
        
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        Product product = productOpt.get();
        User user = userOpt.get();
        
        return product.getUserId().equals(user.getId());
    }
}