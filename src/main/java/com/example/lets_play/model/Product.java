package com.example.lets_play.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {
  @Id
  private String id;
  private String name;
  private String description;
  private Double price;
  private String userId;
  // constructors, getters, setters
}
