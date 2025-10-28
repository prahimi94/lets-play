#!/bin/bash

# MongoDB Injection Prevention Demo
# This script demonstrates various injection attempts and how they are blocked

echo "=== MongoDB Injection Prevention Demo ==="
echo "This script tests the security measures implemented in the Let's Play application"
echo ""

# Start the application (assumes it's running on localhost:8080)
BASE_URL="http://localhost:8080"

echo "1. Testing Product Creation with Injection Attempts"
echo "=================================================="

# Test 1: MongoDB $where injection attempt
echo "❌ Attempting injection with \$where clause..."
curl -s -X POST "${BASE_URL}/api/products" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "name": "$where: function() { return true; }",
    "description": "This should be blocked",
    "price": 100.00
  }' | echo "Response: $(cat)"

echo ""

# Test 2: JavaScript function injection
echo "❌ Attempting JavaScript function injection..."
curl -s -X POST "${BASE_URL}/api/products" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "name": "function() { return this.password; }",
    "description": "Malicious JavaScript code",
    "price": 100.00
  }' | echo "Response: $(cat)"

echo ""

# Test 3: MongoDB operator injection
echo "❌ Attempting MongoDB operator injection..."
curl -s -X POST "${BASE_URL}/api/products" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "name": "{$ne: null}",
    "description": "MongoDB operator injection",
    "price": 100.00
  }' | echo "Response: $(cat)"

echo ""

# Test 4: SQL-style injection attempt
echo "❌ Attempting SQL-style injection..."
curl -s -X POST "${BASE_URL}/api/products" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "name": "\"; DROP TABLE products; --",
    "description": "SQL injection attempt",
    "price": 100.00
  }' | echo "Response: $(cat)"

echo ""

# Test 5: Valid product creation
echo "✅ Testing valid product creation..."
curl -s -X POST "${BASE_URL}/api/products" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "name": "iPhone 15 Pro",
    "description": "Latest Apple smartphone with advanced features",
    "price": 999.99
  }' | echo "Response: $(cat)"

echo ""
echo ""

echo "2. Testing Search Functionality"
echo "==============================="

# Test 6: Search injection attempt
echo "❌ Attempting search injection..."
curl -s -X GET "${BASE_URL}/api/products/search?q=\$where:%20function()%20{%20return%20true;%20}" \
  | echo "Response: $(cat)"

echo ""

# Test 7: Valid search
echo "✅ Testing valid search..."
curl -s -X GET "${BASE_URL}/api/products/search?q=iPhone" \
  | echo "Response: $(cat)"

echo ""
echo ""

echo "3. Testing Authentication with Injection"
echo "========================================"

# Test 8: Login injection attempt
echo "❌ Attempting login injection..."
curl -s -X POST "${BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com\"; $where: \"this.password.length > 0",
    "password": "anything"
  }' | echo "Response: $(cat)"

echo ""

# Test 9: Registration injection attempt
echo "❌ Attempting registration injection..."
curl -s -X POST "${BASE_URL}/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "function() { return db.users.find(); }",
    "email": "hacker@evil.com",
    "password": "password123",
    "role": "ADMIN"
  }' | echo "Response: $(cat)"

echo ""
echo ""

echo "4. Testing Path Parameter Injection"
echo "==================================="

# Test 10: Invalid ObjectId injection
echo "❌ Attempting ObjectId injection..."
curl -s -X DELETE "${BASE_URL}/api/products/\$where:%20function()%20{%20return%20true;%20}" \
  | echo "Response: $(cat)"

echo ""

# Test 11: Valid ObjectId
echo "✅ Testing valid ObjectId (should return 404 if product doesn't exist)..."
curl -s -X DELETE "${BASE_URL}/api/products/507f1f77bcf86cd799439011" \
  | echo "Response: $(cat)"

echo ""
echo ""

echo "=== Demo Complete ==="
echo "Summary of Security Measures:"
echo "• Input validation with regex patterns"
echo "• Character sanitization"
echo "• MongoDB injection pattern detection"
echo "• ObjectId format validation"
echo "• Length restrictions"
echo "• Type validation"
echo "• Comprehensive error handling"
echo ""
echo "All injection attempts should be blocked with appropriate error messages."
echo "Only valid inputs should be processed successfully."
