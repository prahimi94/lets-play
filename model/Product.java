package com.example.letsplay.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "products")
public class Product {
  @Id
  private String id;
  private String name;
  private String description;
  private Double price;
  private Instant createdAt;
  // constructors, getters, setters
}
