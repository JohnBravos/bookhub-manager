# BookHub Manager

## 🚀 Quick Overview

BookHub Manager is a full-stack library management system built with
Spring Boot 3 and React.

It supports role-based access (Admin, Librarian, Member), book loans,
reservations, JWT authentication, and provides a documented REST API
via Swagger.

The project demonstrates backend architecture, security, business logic,
and deployment using Docker and cloud platforms.

## Table of Contents
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Build & Deployment](#build--deployment)
  - [Backend Build](#backend-build)
  - [Frontend Build](#frontend-build)
  - [Running Locally](#running-locally)
  - [Docker Deployment](#docker-deployment)
  - [Cloud Deployment](#cloud-deployment)
- [API Documentation](#api-documentation)
- [Environment Configuration](#environment-configuration)

## Features

### Core Functionality
- 📚 **Book Management**: CRUD operations, inventory tracking, status management
- 👥 **User Management**: Role-based access (Admin, Librarian, Member)
- 🔖 **Reservations**: Queue management, automatic fulfillment
- 📅 **Loans**: Borrow/return tracking, renewal, overdue management
- 🔐 **Authentication**: JWT token-based with Spring Security
- 📊 **Statistics**: System and user statistics dashboard
- 📖 **API Documentation**: OpenAPI 3.0 with Swagger UI

### Technical Features
- Role-based access control (RBAC)
- Pagination and sorting support
- Input validation
- Custom exception handling
- Comprehensive logging

## Technology Stack

### Backend
- **Java 21**
- **Spring Boot 3.x**
- **Spring Security** (JWT)
- **Spring Data JPA**
- **MySQL** (Database)
- **Maven** (Build tool)
- **springdoc-openapi** (Swagger/OpenAPI)

### Frontend
- **React 18**
- **Vite**
- **React Router v6**
- **Tailwind CSS**
- **Axios** (HTTP client)
- **npm** (Package manager)

## Prerequisites

### Required Software
- **Java 21** - [Download](https://www.oracle.com/java/technologies/downloads/#java21)
- **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
- **Node.js 18+** - [Download](https://nodejs.org/)
- **MySQL 8.0+** - [Download](https://www.mysql.com/downloads/)
- **Git** - [Download](https://git-scm.com/)
- **(Optional) Docker** - For containerized deployment

### Verify Installation
```bash
# Check Java
java -version

# Check Maven
mvn -version

# Check Node.js
node -v && npm -v

# Check MySQL
mysql --version
```

## Project Structure

```
bookhub-manager/
├── src/                          # Backend (Spring Boot)
│   ├── main/java/com/JohnBravos/bookhub_manager/
│   │   ├── controller/          # REST Controllers with OpenAPI annotations
│   │   ├── service/             # Business logic
│   │   ├── repository/          # Database layer (JPA)
│   │   ├── model/               # Entity classes
│   │   ├── dto/                 # Request/Response DTOs
│   │   ├── security/            # Authentication & JWT
│   │   ├── config/              # Configurations (SwaggerConfig, SecurityConfig)
│   │   └── core/                # Core utilities, enums, exceptions
│   └── test/
├── pom.xml                       # Maven dependencies
├── frontend/                      # React + Vite
│   ├── src/
│   │   ├── pages/               # Page components
│   │   ├── components/          # Reusable components
│   │   ├── api/                 # Axios client
│   │   ├── context/             # React Context (Auth)
│   │   └── hooks/               # Custom hooks
│   ├── package.json
│   └── vite.config.js
├── README.md                     # This file
└── .gitignore
```

## Build & Deployment

### Backend Build

#### 1. **Build JAR file (Development)**
```bash
# Navigate to project root
cd c:\Users\John Bravos\IdeaProjects\bookhub-manager

# Clean and build
mvn clean package -DskipTests

# Output: target/bookhub-manager-1.0.0.jar
```

#### 2. **Run JAR file**
```bash
# Development server (port 8080)
java -jar target/bookhub-manager-1.0.0.jar

# Production (custom port)
java -jar target/bookhub-manager-1.0.0.jar --server.port=8080
```

### Frontend Build

#### 1. **Install dependencies**
```bash
cd frontend
npm install
```

#### 2. **Development server**
```bash
npm run dev
# Access at: http://localhost:5173
```

#### 3. **Production build**
```bash
npm run build
# Creates: dist/ folder with optimized files
```

#### 4. **Preview production build locally**
```bash
npm run preview
```

### Running Locally

#### **Complete Setup (Backend + Frontend)**

```bash
# Terminal 1: Backend
cd c:\Users\John Bravos\IdeaProjects\bookhub-manager
mvn spring-boot:run

# Wait for: "Started Application in X seconds"

# Terminal 2: Frontend
cd c:\Users\John Bravos\IdeaProjects\bookhub-manager\frontend
npm run dev

# Terminal 3: MySQL (if not running as service)
mysql -u root -p bookhub_manager_db
```

#### **Access Points**
- 🌐 **Frontend**: http://localhost:5173
- 📚 **Backend API**: http://localhost:8080/api
- 📖 **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- 📄 **OpenAPI Spec**: http://localhost:8080/v3/api-docs

### Docker Deployment

#### 1. **Build Docker image**
```bash
# Create Dockerfile in project root
cat > Dockerfile << 'EOF'
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

# Build image
docker build -t bookhub-manager:latest .
```

#### 2. **Run Docker container**
```bash
# With MySQL
docker run -d \
  --name bookhub-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/bookhub_manager_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  --network bookhub-network \
  bookhub-manager:latest

# With Docker Compose (recommended)
cat > docker-compose.yml << 'EOF'
version: '3.9'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpass
      MYSQL_DATABASE: bookhub_manager_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - bookhub-network

  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/bookhub_manager_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: rootpass
    depends_on:
      - mysql
    networks:
      - bookhub-network

volumes:
  mysql_data:

networks:
  bookhub-network:
EOF

# Run with Docker Compose
docker-compose up -d
```

### Cloud Deployment

#### **Railway.app (Recommended - Free)**

1. **Push to GitHub**
```bash
git add .
git commit -m "Ready for deployment"
git push origin main
```

2. **Deploy to Railway**
- Go to [railway.app](https://railway.app)
- Click "New Project"
- Select "Deploy from GitHub"
- Choose your repository
- Configure environment variables:
  ```
  SPRING_DATASOURCE_URL=jdbc:mysql://...
  SPRING_DATASOURCE_USERNAME=root
  SPRING_DATASOURCE_PASSWORD=...
  ```
- Deploy!

3. **Access deployed app**
```
https://your-app.railway.app/api/swagger-ui.html
```

#### **Alternative: Heroku, AWS, Azure**
- Follow similar steps with respective service documentation
- Ensure MySQL database is available
- Configure environment variables for database connection

## API Documentation

### Swagger UI
Access the interactive API documentation at:
```
http://localhost:8080/api/swagger-ui.html
```

### Available Endpoints by Controller

| Controller | Endpoints | Auth Required |
|-----------|-----------|---------------|
| **Authentication** | `/auth/register`, `/auth/login`, `/auth/validate` | No (except validate) |
| **Authors** | GET/POST/PUT/DELETE `/authors` | Yes (Admin/Librarian) |
| **Books** | GET/POST/PATCH/DELETE `/books` | Yes (varies) |
| **Loans** | GET/POST/PUT `/loans` | Yes (varies) |
| **Reservations** | GET/POST/PUT `/reservations` | Yes (varies) |
| **Users** | GET/POST/PUT/DELETE `/users` | Yes (Admin/Librarian) |

### Example API Request
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# Response
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGc..."
  }
}

# Use token for protected endpoints
curl -X GET http://localhost:8080/api/books \
  -H "Authorization: Bearer eyJhbGc..."
```

## Environment Configuration

### Backend (application.properties)
```properties
spring.application.name=bookhub-manager
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/bookhub_manager_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT
app.jwt.secret=your_secret_key_min_32_chars_long
app.jwt.expiration=86400000

# Swagger/OpenAPI
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

### Frontend (.env or environment variables)
```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## Database Setup

### Create Database
```sql
CREATE DATABASE IF NOT EXISTS bookhub_manager_db;
USE bookhub_manager_db;

-- Tables are auto-created by Spring JPA
-- Run backend first to initialize schema
```

### Default Admin User (after first run)
```
Username: admin
Password: admin@123
Email: admin@bookhub.com
Role: ADMIN
```

## Development Workflow

### Build & Test Locally
```bash
# Backend tests
mvn test

# Backend compile check
mvn clean compile

# Frontend tests
cd frontend
npm run lint
npm test
```

### Create a new feature
```bash
# 1. Create feature branch
git checkout -b feature/your-feature

# 2. Make changes in code
# 3. Test locally
# 4. Commit and push
git add .
git commit -m "Add your feature"
git push origin feature/your-feature

# 5. Create Pull Request on GitHub
```

## Troubleshooting

### Backend Issues
| Problem | Solution |
|---------|----------|
| Port 8080 in use | `netstat -ano \| findstr :8080` (find process, then kill) |
| Maven build fails | Clear cache: `mvn clean install -U` |
| Database connection error | Check MySQL is running: `mysql -u root -p` |

### Frontend Issues
| Problem | Solution |
|---------|----------|
| Node modules issue | Delete `node_modules` and `npm install` again |
| API calls fail (CORS) | Check backend is running on port 8080 |
| Port 5173 in use | Run on different port: `npm run dev -- --port 5174` |

## Performance Optimization

### Backend
- Use caching for frequently accessed data
- Implement pagination for large datasets
- Add database indexes for common queries

### Frontend
- Build with `npm run build` for production
- Use lazy loading for routes
- Optimize image sizes

## Security Notes

⚠️ **Before deploying to production:**
1. Change default admin password
2. Use strong JWT secret (minimum 32 characters)
3. Enable HTTPS/SSL
4. Set proper CORS headers
5. Implement rate limiting
6. Use environment variables for sensitive data
7. Validate all user inputs

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Create a Pull Request

## License

This project is for educational purposes.

## Support & Documentation

- 📖 **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- 🔍 **API Spec**: http://localhost:8080/v3/api-docs
- 📚 **Spring Boot Docs**: https://spring.io/projects/spring-boot
- ⚛️ **React Docs**: https://react.dev
- 🏗️ **Vite Docs**: https://vitejs.dev

---

**Last Updated**: December 13, 2025

For questions or issues, refer to the API documentation in Swagger UI or check the backend logs.
