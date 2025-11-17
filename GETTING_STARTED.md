# Getting Started with Quarkus Recipes API

## 🚀 Quickest Start (5 Minutes)

### Prerequisites
- Java 17 or higher
- Maven 3.8+

### Run the Application

```bash
# 1. Clone and navigate to the project
cd quarkus-native-demo

# 2. Build the project
mvn clean install

# 3. Start in dev mode (with hot reload)
./run-dev.sh
```

**That's it!** The API is now running at http://localhost:8080

## 🎯 Try the API

### Using cURL

```bash
# Create a recipe
curl -X POST http://localhost:8080/api/recipes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pasta Carbonara",
    "description": "Classic Italian pasta",
    "preparationTimeMinutes": 25,
    "difficulty": "MEDIUM",
    "ingredients": ["pasta", "eggs", "pancetta", "parmesan"],
    "instructions": ["Boil pasta", "Cook pancetta", "Mix with eggs"]
  }'

# List all recipes
curl http://localhost:8080/api/recipes

# Get a specific recipe (replace {id} with actual ID from previous response)
curl http://localhost:8080/api/recipes/{id}

# Check health
curl http://localhost:8080/health
```

### Using Swagger UI

Open your browser: http://localhost:8080/swagger-ui

You'll see an interactive API documentation where you can test all endpoints.

## 📁 Project Structure

```
quarkus-native-demo/
├── domain/              # 🎯 Business logic (no dependencies!)
│   └── Recipe entities, value objects, repository interfaces
├── application/         # 🔧 Use cases
│   └── CreateRecipeUseCase, GetRecipeUseCase, etc.
├── infrastructure/      # 🌐 External world (REST, Database)
│   └── REST controllers, JPA entities
└── bootstrap/           # 🚀 Main application
    └── Configuration and startup
```

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run tests for a specific module
mvn test -pl domain
mvn test -pl application
```

## 🔨 Development Workflow

### Making Changes

1. **Add a new use case:**
   - Define business logic in `domain/`
   - Create use case in `application/`
   - Expose via REST in `infrastructure/`

2. **The code auto-reloads** when you save files (dev mode)

3. **Example: Add Update Recipe**

   a. Add method to `RecipeRepository` interface (domain):
   ```java
   Recipe update(Recipe recipe);
   ```

   b. Implement in `RecipeRepositoryImpl` (infrastructure):
   ```java
   @Override
   @Transactional
   public Recipe update(Recipe recipe) {
       // implementation
   }
   ```

   c. Create `UpdateRecipeUseCase` (application)
   
   d. Add endpoint to `RecipeResource` (infrastructure)

### Checking Your Changes

```bash
# Build (checks compilation)
mvn clean install

# Run tests
mvn test

# Start dev mode and test manually
./run-dev.sh
```

## 🐳 Using PostgreSQL (Production)

### Start PostgreSQL

```bash
# Start PostgreSQL container
docker-compose up -d

# Verify it's running
docker ps
```

### Configure Application

Edit `bootstrap/src/main/resources/application.properties`:

```properties
# Comment out H2 config, uncomment PostgreSQL
quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/recipes
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres
```

Or use the production profile:

```bash
mvn quarkus:dev -Dquarkus.profile=prod
```

## 🏗️ Building Native Image

### Using Docker (Recommended)

```bash
# Build native executable
mvn package -Pnative -DskipTests

# Run the native executable
./bootstrap/target/bootstrap-1.0.0-SNAPSHOT-runner
```

**Benefits:**
- ⚡ Starts in ~0.1 seconds (vs 3+ seconds JVM)
- 💾 Uses ~50MB RAM (vs 200MB+ JVM)
- 📦 Single executable, no JVM needed

### Requirements
- Docker installed (for container build)
- Or GraalVM installed locally

## 📖 Understanding Clean Architecture

### The Dependency Rule

```
Infrastructure ──→ Application ──→ Domain
    (REST)          (Use Cases)    (Business Logic)
```

**Key Principle:** Dependencies point INWARD, never outward.

### Where to Put Your Code

| What you're adding | Where it goes | Example |
|-------------------|---------------|---------|
| Business rule | `domain/` | Recipe validation logic |
| Data operation | `application/` | CreateRecipeUseCase |
| REST endpoint | `infrastructure/rest/` | RecipeResource |
| Database logic | `infrastructure/persistence/` | RecipeRepositoryImpl |
| Configuration | `bootstrap/` | application.properties |

### Why This Matters

✅ **Domain is portable** - No framework dependencies  
✅ **Easy testing** - Test business logic without frameworks  
✅ **Flexible** - Change database/framework without touching domain  
✅ **Clear boundaries** - Each layer has a single responsibility  

## 🤔 Common Questions

### Q: Where do I add validation?

A: Two places:
- **Business validation** → Domain entities (e.g., "can't have negative prep time")
- **Input validation** → DTOs with `@Valid` annotations

### Q: How do I add a new endpoint?

1. Create use case in `application/`
2. Add method to REST resource in `infrastructure/`
3. Use case will be auto-injected via CDI

### Q: How do I query the database?

Define method in `RecipeRepository` interface (domain), implement in `RecipeRepositoryImpl` (infrastructure).

### Q: Can I use Spring instead of Quarkus?

Yes! That's the beauty of Clean Architecture. You'd only change the `infrastructure` and `bootstrap` modules. Domain and application stay the same.

## 🆘 Troubleshooting

### Port 8080 already in use

```bash
# Change port in application.properties
quarkus.http.port=8081
```

### Build fails with "Unsatisfied dependency"

Make sure Jandex plugin is in all module POMs. Already configured in this project.

### Native build fails

Ensure Docker is running, or install GraalVM locally.

## 📚 Next Steps

1. **Read AGENTS.md** - Deep dive into architecture decisions
2. **Add more operations** - Update, Delete, Search recipes
3. **Add a new module** - Users, Shopping Lists, etc.
4. **Deploy** - Container, Kubernetes, or standalone binary

## 💡 Tips

- Use `mvn quarkus:dev` for hot reload during development
- Check `/health` endpoint to verify app is running
- Use Swagger UI for interactive API testing
- Domain tests run fast (no framework overhead)
- Native compilation takes 3-5 minutes first time

---

**Need more help?** Check out:
- [AGENTS.md](./AGENTS.md) - Architecture deep dive
- [README.md](./README.md) - Full project documentation
- [Quarkus Guides](https://quarkus.io/guides/)
