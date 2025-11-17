# Quarkus Native Demo - Clean Architecture

Demo de Quarkus con compilación nativa, implementando Clean Architecture como backend de una base de datos de recetas.

## 🏗️ Arquitectura

Este proyecto implementa un **monolito modular** con **Clean Architecture**, siguiendo el principio de "dependencias hacia adentro":

```
┌─────────────────────────────────────────┐
│         Infrastructure                  │  ← REST, JPA, DB
│  ┌───────────────────────────────────┐  │
│  │       Application                 │  │  ← Use Cases
│  │  ┌─────────────────────────────┐  │  │
│  │  │        Domain               │  │  │  ← Business Logic
│  │  └─────────────────────────────┘  │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

### Módulos

- **`domain/`**: Lógica de negocio pura (sin dependencias externas)
- **`application/`**: Casos de uso y orquestación
- **`infrastructure/`**: Adaptadores externos (REST, persistencia)
- **`bootstrap/`**: Punto de entrada y configuración

📖 **Lee [AGENTS.md](./AGENTS.md) para entender las decisiones de arquitectura en detalle.**

## 🚀 Inicio Rápido

### Prerrequisitos

- Java 17+
- Maven 3.8+
- Docker (opcional, para compilación nativa)

### Ejecutar en Modo Desarrollo

```bash
# Compilar todo el proyecto
mvn clean install

# Ejecutar en modo dev (con hot-reload)
cd bootstrap
mvn quarkus:dev
```

La aplicación estará disponible en:
- API REST: http://localhost:8080/api/recipes
- Swagger UI: http://localhost:8080/swagger-ui
- Health Check: http://localhost:8080/health

### Probar la API

```bash
# Crear una receta
curl -X POST http://localhost:8080/api/recipes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pasta Carbonara",
    "description": "Classic Italian pasta",
    "preparationTimeMinutes": 25,
    "difficulty": "MEDIUM",
    "ingredients": ["pasta", "eggs", "pancetta"],
    "instructions": ["Boil pasta", "Mix ingredients"]
  }'

# Listar todas las recetas
curl http://localhost:8080/api/recipes

# Obtener una receta por ID
curl http://localhost:8080/api/recipes/{id}
```

## 🔧 Compilación Nativa

Compilar a un binario nativo con GraalVM:

```bash
# Usando Docker (recomendado)
mvn package -Pnative -DskipTests

# Ejecutar el binario nativo
./bootstrap/target/bootstrap-1.0.0-SNAPSHOT-runner
```

**Beneficios de Native:**
- ⚡ Arranque ultra rápido (~0.1s vs ~3s JVM)
- 💾 Menor consumo de memoria
- 📦 Binario independiente (no requiere JVM)

## 📝 Estructura del Proyecto

```
quarkus-native-demo/
├── domain/                     # Capa de dominio
│   └── src/main/java/...
│       ├── entity/            # Entidades de negocio
│       ├── valueobject/       # Value Objects
│       └── repository/        # Interfaces de repositorio
├── application/               # Capa de aplicación
│   └── src/main/java/...
│       ├── usecase/          # Casos de uso
│       └── dto/              # DTOs
├── infrastructure/            # Capa de infraestructura
│   └── src/main/java/...
│       ├── persistence/      # Implementaciones JPA
│       └── rest/             # Controllers REST
├── bootstrap/                # Módulo principal
│   └── src/main/resources/
│       └── application.properties
├── pom.xml                   # Parent POM
└── AGENTS.md                 # Documentación de arquitectura
```

## 🧪 Testing

```bash
# Ejecutar tests unitarios
mvn test

# Ejecutar tests de integración
mvn verify
```

## 🔐 Configuración de Base de Datos

Por defecto usa **H2 in-memory** para desarrollo. Para producción, cambiar en `application.properties`:

```properties
# PostgreSQL
quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/recipes
quarkus.datasource.username=postgres
quarkus.datasource.password=yourpassword
```

## 📚 Endpoints Disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/recipes` | Crear una receta |
| GET | `/api/recipes` | Listar todas las recetas |
| GET | `/api/recipes/{id}` | Obtener receta por ID |
| GET | `/health` | Health check |
| GET | `/swagger-ui` | Documentación API |

## 🎯 Próximos Pasos (Vibecoding)

1. **Agregar más operaciones CRUD:**
   - Actualizar recetas (PUT)
   - Eliminar recetas (DELETE)
   - Búsqueda por ingrediente

2. **Expandir el dominio:**
   - Módulo de usuarios
   - Módulo de lista de compras
   - Relaciones entre módulos

3. **Testing:**
   - Tests unitarios del dominio
   - Tests de integración

4. **CI/CD:**
   - GitHub Actions para compilación nativa
   - Docker container deployment

**👉 Consulta [AGENTS.md](./AGENTS.md) para guías detalladas sobre cómo extender el proyecto.**

## 📖 Referencias

- [Quarkus Guides](https://quarkus.io/guides/)
- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Quarkus Native Guide](https://quarkus.io/guides/building-native-image)

## 🤝 Contribuir

Este proyecto sigue principios de Clean Architecture. Antes de contribuir:

1. Lee [AGENTS.md](./AGENTS.md) para entender la arquitectura
2. Asegúrate de que el dominio permanezca puro (sin dependencias externas)
3. Sigue el flujo: Infrastructure → Application → Domain

## 📄 Licencia

MIT License
