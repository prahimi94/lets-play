package com.example.lets_play.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;

@Document(collection = "users")
public class User {
  @Id
  private String id;
  
  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
  @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name can only contain letters and spaces")
  private String name;
  
  @NotBlank(message = "Email is required")
  @Email(message = "Please provide a valid email address")
  @Size(max = 100, message = "Email must be less than 100 characters")
  private String email;
  
  @NotBlank(message = "Password is required")
  private String password; // hashed
  
  @NotBlank(message = "Role is required")
  @Pattern(regexp = "^(USER|ADMIN)$", message = "Role must be either USER or ADMIN")
  private String role; // "USER" or "ADMIN"

  // Constructors
  public User() {}
  public User(String name, String email, String password, String role) {
      this.name = name;
      this.email = email;
      this.password = password;
      this.role = role;
  }

  public String getId() {
      return id;
  }
  public void setId(String id) {
      this.id = id;
  }
  public String getName() {
      return name;
  }
  public void setName(String name) {
      this.name = name;
  }
  public String getEmail() {
      return email;
  }
  public void setEmail(String email) {  
      this.email = email;
  }
  public String getPassword() {
      return password;
  }
  public void setPassword(String password) {
      this.password = password;
  }
  public String getRole() {
      return role;
  }
  public void setRole(String role) {
      this.role = role;
  }
}
