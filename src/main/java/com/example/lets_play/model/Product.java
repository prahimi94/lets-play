package com.example.lets_play.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.*;

@Document(collection = "products")
public class Product {
    @Id
    private String id;

    @Field("name")
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_.]+$", message = "Product name contains invalid characters")
    private String title;

    @NotBlank(message = "Product description is required")
    @Size(min = 10, max = 500, message = "Product description must be between 10 and 500 characters")
    private String description;

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Product price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Product price must be less than 1,000,000")
    private Double price;

    @NotBlank(message = "User ID is required")
    @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid user ID format")
    private String userId;

    public Product() {}
    public Product(String title, String description, Double price, String userId) {
            this.title = title;
            this.description = description;
            this.price = price;
            this.userId = userId;
    }

    public String getId() {
            return id;
    }
    public void setId(String id) {
            this.id = id;
    }
    public String getTitle() {
            return title;
    }
    public void setTitle(String title) {
            this.title = title;
    }
    public String getDescription() {
            return description;
    }
    public void setDescription(String description) {
            this.description = description;
    }
    public Double getPrice() {
            return price;
    }
    public void setPrice(Double price) {
            this.price = price;
    }
    public String getUserId() {
            return userId;
    }
    public void setUserId(String userId) {
            this.userId = userId;
    }
}
