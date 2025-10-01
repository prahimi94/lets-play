package com.example.lets_play.repository;

import com.example.lets_play.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
  List<Product> findByNameContainingIgnoreCase(String q);
}
