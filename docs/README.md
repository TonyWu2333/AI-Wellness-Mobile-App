# Wellness App Backend

## Environment Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+ (optional; H2 in-memory database used by default for development)

### Run with H2 (default)
```bash
cd backend
mvn spring-boot:run
```

H2 Console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:wellnessdb`
- Username: `sa`
- Password: (empty)

### Run with MySQL
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

### API Base URL
`http://localhost:8080`

## Project Structure
```
backend/
├── pom.xml
├── src/main/java/com/wellnessapp/
│   ├── WellnessApplication.java      # Main entry point
│   ├── config/                        # Spring configuration
│   ├── controller/                    # REST API controllers
│   ├── dto/                           # Data Transfer Objects
│   ├── entity/                        # JPA entities
│   ├── repository/                    # Spring Data repositories
│   ├── security/                      # JWT & Spring Security
│   └── service/                       # Business logic
└── src/main/resources/
    └── application.yml                # Application configuration
```
