# Clean Architecture en Quarkus Native - Decisiones de Arquitectura

## 📋 Índice
1. [Visión General](#visión-general)
2. [Arquitectura Limpia - Principios](#arquitectura-limpia---principios)
3. [Estructura de Módulos](#estructura-de-módulos)
4. [Dependencias y Flujo](#dependencias-y-flujo)
5. [Optimizaciones para Native Compilation](#optimizaciones-para-native-compilation)
6. [Cómo Continuar (Vibecoding)](#cómo-continuar-vibecoding)

---

## 🎯 Visión General

Este documento describe la arquitectura limpia (Clean Architecture)
aplicada a un monolito modular desarrollado con **Quarkus** sobre **Java
21**.\
El diseño mantiene el principio de *dependencias hacia adentro*: el
**Dominio** no depende de nada, la **Aplicación** depende del dominio y
define la orquestación, y la **Infraestructura** provee las
implementaciones técnicas sin alterar la lógica de negocio.

El objetivo es lograr un backend **modular, altamente testeable,
desacoplado del framework** y escalable.


### Objetivos Principales
- ✅ Separación clara de responsabilidades por capas
- ✅ Independencia del framework en el núcleo del dominio
- ✅ Testabilidad y mantenibilidad
- ✅ Compilación nativa optimizada (GraalVM)
- ✅ Base sólida para expansión modular

---

## 🏛️ Arquitectura Limpia - Principios

### Regla de Dependencia
```
┌─────────────────────────────────────────┐
│         Infrastructure                  │  ← Frameworks, DB, REST
│  ┌───────────────────────────────────┐  │
│  │       Application                 │  │  ← Use Cases, DTOs
│  │  ┌─────────────────────────────┐  │  │
│  │  │        Domain               │  │  │  ← Entities, Business Logic
│  │  │   (Core Business)           │  │  │
│  │  └─────────────────────────────┘  │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘

Dependencias: Infrastructure → Application → Domain
```

### Principios Aplicados

1. **Dependency Inversion Principle (DIP)**
   - El dominio define interfaces (ej: `RecipeRepository`)
   - La infraestructura implementa esas interfaces (ej: `RecipeRepositoryImpl`)
   - El dominio nunca depende de la infraestructura

2. **Single Responsibility Principle (SRP)**
   - Cada capa tiene una responsabilidad única y bien definida
   - Separación clara entre lógica de negocio y detalles técnicos

3. **Open/Closed Principle (OCP)**
   - Fácil agregar nuevas funcionalidades sin modificar código existente
   - Nuevos casos de uso se agregan sin tocar el dominio


### Principios No Negociables

1.  Dominio sin frameworks.
2.  Use cases puros.
3.  Errores tipados.
4.  Puertos en application, adaptadores en infrastructure.
5.  Entidades JPA ≠ Entidades dominio.
6.  Controladores sin lógica.
7.  Mappers explícitos.
8.  Wiring explícito.
9.  Testing por niveles.
10. El dominio manda.

---

## 📦 Estructura de Módulos

### 1. **Domain** (Capa Más Interna)
**Ubicación:** `/domain`

**Responsabilidades:**
- Entidades del negocio (`Recipe`, `DifficultyLevel`)
- Value Objects (`RecipeId`)
- Interfaces de repositorios (contratos, no implementaciones)
- Lógica de negocio pura

**Dependencias:**
- ❌ **NINGUNA** dependencia externa
- ✅ Solo Java puro + Jakarta Validation API (para anotaciones)

**Ejemplo de código:**
```java
// domain/entity/Recipe.java
public class Recipe {
    private final RecipeId id;
    private String name;
    
    public void updateDetails(String name, ...) {
        // Lógica de negocio pura
    }
    
    public boolean isQuickRecipe() {
        return preparationTimeMinutes <= 30;
    }
}
```

**Decisión Clave:** El dominio no conoce Hibernate, JPA, REST, o cualquier framework. Es 100% portable.

---

### 2. **Application** (Capa de Casos de Uso)
**Ubicación:** `/application`

**Responsabilidades:**
- Casos de uso (orquestación de lógica de negocio)
- DTOs para transferencia de datos
- Coordinación entre dominio e infraestructura

**Dependencias:**
- ✅ `domain` (capa interna)
- ✅ Quarkus Arc (CDI para inyección de dependencias)
- ✅ Hibernate Validator

**Ejemplo de código:**
```java
// application/usecase/CreateRecipeUseCase.java
@ApplicationScoped
public class CreateRecipeUseCase {
    private final RecipeRepository repository;
    
    public RecipeDTO execute(RecipeDTO dto) {
        Recipe recipe = new Recipe(...);
        Recipe saved = repository.save(recipe);
        return mapToDTO(saved);
    }
}
```

**Decisión Clave:** Los casos de uso orquestan, no implementan lógica de negocio. La lógica vive en el dominio.

---

### 3. **Infrastructure** (Capa Externa)
**Ubicación:** `/infrastructure`

**Responsabilidades:**
- Implementaciones de repositorios (JPA/Panache)
- Controllers REST
- Configuración de frameworks
- Adaptadores a sistemas externos

**Dependencias:**
- ✅ `application` y `domain`
- ✅ Quarkus REST (Resteasy Reactive)
- ✅ Hibernate ORM + Panache
- ✅ PostgreSQL/H2 drivers
- ✅ SmallRye Health, OpenAPI

**Ejemplo de código:**
```java
// infrastructure/persistence/RecipeRepositoryImpl.java
@ApplicationScoped
public class RecipeRepositoryImpl implements RecipeRepository, PanacheRepository<RecipeEntity> {
    public Recipe save(Recipe recipe) {
        RecipeEntity entity = toEntity(recipe);
        this.persist(entity);
        return toDomain(entity);
    }
}
```

**Decisión Clave:** La infraestructura es intercambiable. Podríamos cambiar de JPA a MongoDB sin tocar dominio o aplicación.

---

### 4. **Bootstrap** (Punto de Entrada)
**Ubicación:** `/bootstrap`

**Responsabilidades:**
- Configuración de la aplicación (`application.properties`)
- Punto de entrada de Quarkus
- Agregación de todos los módulos

**Dependencias:**
- ✅ Todas las capas (ensambla la aplicación)

**Decisión Clave:** Este módulo es el único que genera el ejecutable. Los demás son librerías.

---

## 🔄 Dependencias y Flujo

### Flujo de una Request HTTP

```
1. HTTP Request
   ↓
2. RecipeResource (Infrastructure/REST)
   ↓
3. CreateRecipeUseCase (Application)
   ↓
4. Recipe Entity (Domain) ← Lógica de negocio
   ↓
5. RecipeRepository Interface (Domain)
   ↓
6. RecipeRepositoryImpl (Infrastructure/Persistence)
   ↓
7. Database
```

### Maven Dependency Graph

```
bootstrap
├── infrastructure
│   ├── application
│   │   └── domain
│   └── domain
└── (agrega todo)
```

**Validación de Dependencias:**
```bash
# Verificar que domain no tenga dependencias externas
mvn dependency:tree -pl domain

# Debe mostrar solo:
# - jakarta.validation-api (scope: provided)
# - junit (scope: test)
```
### Extensiones comunes

-   RESTEasy Reactive
-   Hibernate ORM
-   Flyway
-   OpenAPI
-   Health Check
-   Scheduler

### 5. Documentación asociada importante.

* analisis a tener en cuenta: [aqui](doc/analisis-funcional.md)

---

## ⚡ Optimizaciones para Native Compilation

### 1. **Configuración de POM Parent**
```xml
<properties>
    <quarkus.platform.version>3.6.4</quarkus.platform.version>
    <quarkus.native.container-build>true</quarkus.native.container-build>
</properties>
```

**Por qué:** Usamos container-build para no requerir GraalVM local.

### 2. **Profile Native**
```xml
<profile>
    <id>native</id>
    <properties>
        <quarkus.package.type>native</quarkus.package.type>
    </properties>
</profile>
```

**Uso:**
```bash
# Compilación nativa
mvn clean package -Pnative -DskipTests

# Ejecutar el binario nativo
./bootstrap/target/bootstrap-1.0.0-SNAPSHOT-runner
```

### 3. **application.properties Optimizado**
```properties
# Inicialización en runtime para H2 (compatibilidad nativa)
quarkus.native.additional-build-args=\
  --initialize-at-run-time=org.h2.store.fs.FilePathDisk

# HTTP URL handler habilitado
quarkus.native.enable-http-url-handler=true
```

**Por qué:** GraalVM requiere configuración explícita para reflexión, recursos, y clases que se inicializan en runtime.

### 4. **Hibernate ORM + Panache**
- ✅ Panache reduce código boilerplate
- ✅ Compatible con native desde Quarkus 1.0+
- ✅ No usa reflexión intensiva como JPA tradicional

### 5. **Base de Datos**
- **Dev/Test:** H2 in-memory (rápido, sin Docker)
- **Prod:** PostgreSQL (cambiar en `application.properties`)

```properties
# Para producción (cambiar estas líneas)
quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/recipes
```

---

## 🚀 Cómo Continuar (Vibecoding)

### Fase 1: Testing y Validación
```bash
# 1. Compilar todos los módulos
cd /ruta/al/proyecto
mvn clean install

# 2. Ejecutar en modo dev (hot reload)
cd bootstrap
mvn quarkus:dev

# 3. Probar endpoints
curl http://localhost:8080/health
curl http://localhost:8080/api/recipes

# 4. Ver Swagger UI
# Abrir: http://localhost:8080/swagger-ui
```

### Fase 2: Agregar Nuevas Funcionalidades

#### Ejemplo: Agregar búsqueda por ingrediente

1. **Domain:** Agregar método en `RecipeRepository`
```java
List<Recipe> findByIngredient(String ingredient);
```

2. **Infrastructure:** Implementar en `RecipeRepositoryImpl`
```java
public List<Recipe> findByIngredient(String ingredient) {
    return RecipeEntity.list("SELECT DISTINCT r FROM RecipeEntity r " +
        "JOIN r.ingredients i WHERE LOWER(i) LIKE LOWER(?1)", 
        "%" + ingredient + "%")
        .stream().map(this::toDomain).collect(Collectors.toList());
}
```

3. **Application:** Crear `SearchRecipesByIngredientUseCase`
```java
@ApplicationScoped
public class SearchRecipesByIngredientUseCase {
    public List<RecipeDTO> execute(String ingredient) {
        return repository.findByIngredient(ingredient)
            .stream().map(this::mapToDTO).collect(Collectors.toList());
    }
}
```

4. **Infrastructure:** Agregar endpoint en `RecipeResource`
```java
@GET
@Path("/search/ingredient")
public List<RecipeDTO> searchByIngredient(@QueryParam("q") String ingredient) {
    return searchUseCase.execute(ingredient);
}
```

### Fase 3: Modularización Avanzada

Para escalar a un monolito modular más complejo:

```
project-root/
├── domain/
│   ├── recipes/          ← Módulo de recetas
│   ├── users/            ← Módulo de usuarios
│   └── shopping/         ← Módulo de lista de compras
├── application/
│   ├── recipes/
│   ├── users/
│   └── shopping/
└── infrastructure/
    ├── recipes/
    ├── users/
    └── shopping/
```

**Regla:** Los módulos del dominio NO deben depender entre sí. Usar eventos o casos de uso compartidos.

### Fase 4: CI/CD con Native

```yaml
# .github/workflows/native-build.yml
- name: Build Native Image
  run: mvn package -Pnative -DskipTests -Dquarkus.native.container-build=true

- name: Test Native Image
  run: |
    ./bootstrap/target/*-runner &
    sleep 5
    curl http://localhost:8080/health
```

---

## 🎓 Conceptos Clave para Recordar

### 1. **Clean Architecture NO es:**
- ❌ Solo separar en carpetas
- ❌ Complejidad innecesaria
- ❌ Muchas interfaces por el gusto

### 2. **Clean Architecture SÍ es:**
- ✅ Dominio puro e independiente
- ✅ Dependencias hacia adentro
- ✅ Testabilidad y mantenibilidad
- ✅ Flexibilidad para cambiar tecnologías

### 3. **Cuándo agregar una nueva capa:**
- 🤔 ¿La funcionalidad es lógica de negocio? → **Domain**
- 🤔 ¿Es orquestación/coordinación? → **Application**
- 🤔 ¿Es detalle técnico (REST, DB)? → **Infrastructure**

---

## 📚 Referencias

- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Quarkus Native Guide](https://quarkus.io/guides/building-native-image)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design - Eric Evans](https://www.domainlanguage.com/ddd/)

---

## 🛠️ Comandos Útiles

```bash
# Desarrollo con hot-reload
mvn quarkus:dev

# Compilar todos los módulos
mvn clean install

# Compilación nativa (requiere Docker o GraalVM)
mvn package -Pnative -DskipTests

# Ejecutar nativo
./bootstrap/target/bootstrap-1.0.0-SNAPSHOT-runner

# Ver dependencias de un módulo
mvn dependency:tree -pl domain

# Analizar el binario nativo
ls -lh bootstrap/target/*-runner
```

---

## ✅ Checklist de Validación

Antes de desplegar o hacer cambios grandes, verifica:

- [ ] El módulo `domain` NO tiene dependencias de frameworks
- [ ] Los repositorios están definidos como interfaces en `domain`
- [ ] Los casos de uso están en `application`, no en `infrastructure`
- [ ] Los DTOs no se usan en el `domain` (solo entidades puras)
- [ ] El proyecto compila con `mvn clean install`
- [ ] La compilación nativa funciona: `mvn package -Pnative`
- [ ] Los endpoints REST están documentados en Swagger
- [ ] Los health checks responden correctamente

---

**¡Feliz vibecoding! 🎸**

Si tienes dudas sobre dónde colocar algo:
1. ¿Es lógica de negocio pura? → **Domain**
2. ¿Coordina el dominio? → **Application**
3. ¿Es un detalle técnico? → **Infrastructure**
