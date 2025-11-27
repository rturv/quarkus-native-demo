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
- Documentación OpenAPI (JSON): http://localhost:8080/q/openapi?format=json
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
# Opción 1: Script automatizado (RECOMENDADO)
./build-native.sh              # Compilar + imagen
./build-native.sh --compose    # Compilar + imagen + docker compose
./build-native.sh --run        # Compilar + imagen + ejecutar contenedor

# Opción 2: Manual con Maven
mvn clean package -Pnative -DskipTests -Dquarkus.native.container-build=true

# Opción 3: Construir solo imagen (si binario ya existe)
docker build -f Dockerfile.native -t quarkus-recipes:native .
```

### 📊 Benchmarks Reales

| Métrica | JDK | Native |
|---------|-----|--------|
| **Startup** | 10-15s | 2.851s ⚡ |
| **Memory (idle)** | 200+ MB | ~44 MB 💾 |
| **Docker image** | 323 MB | 282 MB 📦 |
| **Binary** | JAR 80MB | ELF 123 MB |

**Ejecutar imagen nativa:**

```bash
# Con docker compose (recomendado - incluye BD real)
docker compose -f docker-compose.native.yml up -d

# O manualmente
docker run -d \
  --name quarkus-native \
  -p 8080:8080 \
  -e QUARKUS_DATASOURCE_JDBC_URL="jdbc:postgresql://..." \
  -e QUARKUS_DATASOURCE_USERNAME="user" \
  -e QUARKUS_DATASOURCE_PASSWORD="pass" \
  quarkus-recipes:native
```

**📖 Documentación completa:** [README.native.md](./README.native.md)

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

# Verificar tests REST con mocks JWT y anotaciones Swagger
mvn -pl infrastructure -am test
```

La suite que reside en `infrastructure` ahora cubre el endpoint `/api/auth/refresh`, por lo que `mvn -pl infrastructure -am test` sirve para validar que la renovación de JWT responde `200` con un nuevo token válido y `401` cuando la autenticación falla.

## 📬 Colección Postman

- La colección `test/quarkus-recipes.postman_collection.json` contiene peticiones para `register`, `login`, `refresh` y operaciones sobre recetas protegidas con JWT.
- Importa el archivo en Postman y define dos variables globales:
   - `{{base_url}}` apunta al host donde tienes el servicio (por ejemplo `http://localhost:8080`).
   - `{{jwt_token}}` se actualiza manualmente con el token devuelto en la respuesta de login o refresh antes de ejecutar endpoints que lo necesitan.

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
| POST | `/api/auth/register` | Registrar usuario y recibir JWT |
| POST | `/api/auth/login` | Login y retorno de JWT simétrico |
| POST | `/api/auth/refresh` | Refresca el JWT del usuario autenticado y entrega un nuevo token |
| GET | `/health` | Health check |
| GET | `/swagger-ui` | Documentación API |

## 🗺️ Documentación REST y seguridad

- Todos los `@Path` están documentados con anotaciones OpenAPI (`@Tag`, `@Operation`, `@APIResponse`) para que `swagger-ui` muestre respuesta tentativa, request body y códigos HTTP.
- La documentación también expone los esquemas asociados (`UsuarioDTO`, `ComentarioDTO`, `LoginRequest`) y muestra claramente qué campos se consideran obligatorios o generados automáticamente (como `jwt`).
- `AuthResource` describe la generación de tokens JWT simétricos mediante `JwtTokenService` mientras que `ComentariosResource` (y demás controladores) usan `AuthenticatedUser` para los endpoints protegidos.
-- Los tests RestAssured en `infrastructure` simulan `JwtTokenService` con `QuarkusMock.installMockForType` y validan los contratos documentados sin depender de un proveedor externo.
-- Los tests RestAssured en `infrastructure` simulan `JwtTokenService` con `QuarkusMock.installMockForType` y validan los contratos documentados sin depender de un proveedor externo.

### Refresh token y seguridad

- **Ruta:** `POST /api/auth/refresh`, protegida con `@RolesAllowed({"user","admin"})`, no requiere body pero sí headers `Content-Type: application/json` y `Authorization: Bearer {token}`.
- **Respuesta esperada:** `200` con `UsuarioDTO` que incluye el nuevo `jwt` generado por `JwtTokenService` y `claveAcceso` siempre en `null`; `401` si el token actual está expirado o inválido.
- **Pruebas:** `AuthResourceTest` valida el refresco exitoso y los casos no autorizados usando `QuarkusMock` y `@TestSecurity`, asegurando que la seguridad descrita en la documentación se mantiene.

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
