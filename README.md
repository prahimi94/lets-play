# Let's Play API 🎮

A secure REST API built with Spring Boot for user and product management, featuring JWT authentication, MongoDB integration, and comprehensive security measures.

## 🚀 Features

- **User Management**: Registration, authentication, and profile management
- **Product Management**: CRUD operations with ownership-based access control
- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: Admin and User roles with different permissions
- **MongoDB Integration**: NoSQL database with secure query operations
- **Input Validation & Sanitization**: Protection against injection attacks
- **Comprehensive Error Handling**: Proper HTTP status codes and error messages

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.5.6
- **Database**: MongoDB 6
- **Authentication**: JWT (JSON Web Tokens)
- **Security**: Spring Security with method-level authorization
- **Build Tool**: Maven
- **Java Version**: 17

## 📋 Prerequisites

- Java 17 or higher
- Docker (for MongoDB)
- Maven 3.6+ (or use included Maven wrapper)

## 🏃‍♂️ Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd lets-play
```

### 2. Start MongoDB Container
```bash
docker run -d --name letsplay-mongo -p 27017:27017 -e MONGO_INITDB_DATABASE=letsplay mongo:6
```

Or using Docker Compose:
```bash
docker-compose up -d
```

### 3. Run the Application
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

## 🔒 HTTPS Configuration

### Development (HTTP)
By default, the application runs on HTTP for development:
```bash
./mvnw spring-boot:run
```
Available at: `http://localhost:8080`

### Production (HTTPS)

#### 1. Generate SSL Certificate
```bash
./generate-ssl.sh
```

#### 2. Run with HTTPS
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```
Available at: `https://localhost:8443`

#### 3. Configuration Options

**Environment Variables:**
- `APP_ENFORCE_HTTPS=true` - Force HTTPS redirects
- `SERVER_PORT=8443` - HTTPS port
- `SERVER_SSL_KEY_STORE_PASSWORD=your_password` - Keystore password

**Production Deployment:**
1. Replace the self-signed certificate with a real SSL certificate
2. Set `app.enforce-https=true` in production
3. Configure your reverse proxy (nginx/Apache) for SSL termination

#### 4. Security Headers
When HTTPS is enabled, the following security headers are automatically added:
- `Strict-Transport-Security`: Forces HTTPS for 1 year
- `Content-Security-Policy: upgrade-insecure-requests`: Upgrades HTTP to HTTPS
- `X-Frame-Options: DENY`: Prevents clickjacking
- `X-Content-Type-Options: nosniff`: Prevents MIME sniffing
- `X-XSS-Protection`: XSS protection

### 4. Default Admin User
On first startup, a default admin user is automatically created:
- **Email**: `admin@letsplay.com`
- **Password**: `Admin123*`

## 📚 API Endpoints

### Authentication Endpoints
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `POST` | `/api/auth/register` | User registration | Public |
| `POST` | `/api/auth/login` | User login | Public |
| `POST` | `/api/auth/register-admin` | Admin registration (only if no admin exists) | Public |

### User Management Endpoints
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `GET` | `/api/users` | Get all users | Admin only |
| `GET` | `/api/users/me` | Get current user profile | Authenticated |
| `GET` | `/api/users/debug-auth` | Debug authentication info | Authenticated |
| `GET` | `/api/users/admin-only` | Test admin access | Admin only |
| `POST` | `/api/users` | Create new user | Admin only |
| `PUT` | `/api/users/{id}` | Update user | Admin only |
| `PUT` | `/api/users/updatePassword` | Update own password | Authenticated |

### Product Endpoints
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `GET` | `/api/products` | Get all products | Public |
| `GET` | `/api/products/search?q={query}` | Search products | Public |
| `GET` | `/api/products/{id}` | Get product by ID | Owner or Admin |
| `GET` | `/api/products/{id}/details` | Get detailed product info | Owner, Admin, or price < $100 |
| `POST` | `/api/products` | Create new product | Authenticated |
| `PUT` | `/api/products/{id}` | Update product | Owner or Admin |
| `DELETE` | `/api/products/{id}` | Delete product | Owner or Admin |

## 🔐 Authentication

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@letsplay.com",
    "password": "Admin123*"
  }'
```

### Using JWT Token
Include the JWT token in the Authorization header:
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## 📝 Request/Response Examples

### Register New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "SecurePass123!"
  }'
```

### Create Product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Gaming Laptop",
    "description": "High-performance laptop for gaming",
    "price": 1299.99
  }'
```

### Search Products
```bash
curl -X GET "http://localhost:8080/api/products/search?q=laptop"
```

## 🛡️ Security Features

- **Password Hashing**: BCrypt encryption for all passwords
- **JWT Security**: Secure token-based authentication with 24-hour expiration
- **Input Sanitization**: Protection against MongoDB injection attacks
- **Role-Based Access**: Fine-grained permissions using Spring Security
- **Password Protection**: Passwords never returned in API responses
- **Request Validation**: Comprehensive input validation using Bean Validation
- **Error Handling**: Secure error messages without information leakage

## 🔧 Configuration

### Database Configuration
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/letsplay
```

### Security Configuration
```properties
# Enable proper 404 handling
spring.mvc.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false
```

## 🚨 Security Testing

The project includes a security demonstration script:
```bash
./security-demo.sh
```

This script tests various security scenarios including:
- Input validation
- MongoDB injection attempts
- Authentication and authorization
- Error handling

## 🐳 Docker Support

### Using Docker Compose
```yaml
version: "3.9"
services:
  mongo:
    image: mongo:6
    container_name: letsplay-mongo
    restart: always
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_DATABASE: letsplay
    volumes:
      - mongo_data:/data/db

volumes:
  mongo_data:
```

### Manual MongoDB Setup
```bash
# Start MongoDB container
docker run -d --name letsplay-mongo -p 27017:27017 -e MONGO_INITDB_DATABASE=letsplay mongo:6

# Connect to MongoDB shell
docker exec -it letsplay-mongo mongosh letsplay

# View collections
show collections

# Query users
db.users.find().pretty()

# Query products
db.products.find().pretty()
```

## 📊 Database Schema

### User Document
```json
{
  "_id": "ObjectId",
  "name": "String (2-50 chars, letters only)",
  "email": "String (valid email, max 100 chars)",
  "password": "String (BCrypt hashed)",
  "role": "String (USER|ADMIN)"
}
```

### Product Document
```json
{
  "_id": "ObjectId",
  "name": "String (2-100 chars)",
  "description": "String (10-500 chars)",
  "price": "Double (0.01-999999.99)",
  "userId": "String (ObjectId reference)"
}
```

## 🧪 Testing

### Run Tests
```bash
./mvnw test
```

### Manual API Testing
Use the included examples or tools like Postman, curl, or any REST client.

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/example/lets_play/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── exception/      # Exception handling
│   │   ├── model/          # Entity models
│   │   ├── repository/     # MongoDB repositories
│   │   ├── security/       # Security configuration
│   │   └── service/        # Business logic services
│   └── resources/
│       └── application.properties
└── test/                   # Test classes
```

## 🚀 Deployment

### Building for Production
```bash
./mvnw clean package
java -jar target/lets-play-0.0.1-SNAPSHOT.jar
```

### Environment Variables
```bash
export MONGODB_URI=mongodb://your-mongo-host:27017/letsplay
export JWT_SECRET=your-secret-key
export SERVER_PORT=8080
```

## 📜 License

This project is created for educational purposes.

## 👥 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📞 Support

For questions or issues, please create an issue in the repository.

---

**Made with ❤️ using Spring Boot**
