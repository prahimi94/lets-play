#!/bin/bash

# Generate SSL Certificate for Development
# This creates a self-signed certificate for HTTPS testing

echo "🔐 Generating self-signed SSL certificate for development..."

# Create keystore directory if it doesn't exist
mkdir -p src/main/resources

# Generate self-signed certificate
keytool -genkeypair \
    -alias tomcat \
    -keyalg RSA \
    -keysize 2048 \
    -storetype PKCS12 \
    -keystore src/main/resources/keystore.p12 \
    -validity 365 \
    -dname "CN=localhost, OU=Development, O=LetsPlay, L=City, ST=State, C=US" \
    -storepass changeit \
    -keypass changeit

echo "✅ SSL certificate generated!"
echo "📝 Certificate details:"
echo "   - Keystore: src/main/resources/keystore.p12"
echo "   - Password: changeit"
echo "   - Alias: tomcat"
echo "   - Valid for: 365 days"
echo ""
echo "🚀 To use HTTPS:"
echo "   1. Run with production profile: ./mvnw spring-boot:run -Dspring-boot.run.profiles=prod"
echo "   2. Access at: https://localhost:8443"
echo ""
echo "⚠️  Note: Browsers will show security warning for self-signed certificates"
echo "   Click 'Advanced' → 'Proceed to localhost (unsafe)' to continue"