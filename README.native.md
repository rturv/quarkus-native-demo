# 🚀 Quarkus Native Image - Guía Completa

## ¿Qué es una Imagen Nativa?

Una **imagen nativa de Quarkus** es un ejecutable compilado a código máquina usando **GraalVM Native Image**. Características:

- ⚡ **Startup en <3 segundos** (vs 10-15s con JVM)
- 💾 **~44 MB RAM en uso** (vs 200+ MB con JVM)
- 📦 **Binario autónomo** (no necesita JVM)
- 🔒 **Mejor seguridad** (menos superficie de ataque)

---

## 📊 Benchmarks Reales (Este Proyecto)

### Compilación Nativa
```bash
# Tiempo de compilación (con container-build)
Compilación total: ~2 minutos
Compilación nativo (GraalVM): ~1.5 minutos
```

### Rendimiento en Runtime
```
┌─────────────────────────────────────────────┐
│ Métrica          │ JDK      │ Native       │
├─────────────────────────────────────────────┤
│ Startup          │ 10-15s   │ 2.851s ⚡    │
│ Memory (idle)    │ 200+ MB  │ ~44 MB 💾    │
│ Docker image     │ 323 MB   │ 282 MB 📦    │
│ Binary size      │ JAR 80MB │ ELF 123 MB   │
└─────────────────────────────────────────────┘
```

---

## 🏗️ Cómo Compilar Nativo

### Opción 1: Compilación Local (Recomendada)

Requiere **Maven 3.9+** y **Docker** (para container-build):

```bash
# 1. Actualizar Maven a 3.9+
mkdir -p ~/maven
cd ~/maven
curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz | tar xz
export PATH=~/maven/apache-maven-3.9.6/bin:$PATH

# 2. Compilar nativo con container-build
cd /path/to/quarkus-native-demo
mvn clean package -Pnative -DskipTests -Dquarkus.native.container-build=true

# 3. Verificar binario
ls -lh bootstrap/target/*-runner
# Output: -rwxrwxrwx 1 user user 123M ... bootstrap-1.0.0-SNAPSHOT-runner
```

**Tiempo esperado:** ~2-3 minutos (primera vez), ~1.5 minutos (builds posteriores)

### Opción 2: Compilación en Docker

No requiere GraalVM local, pero es más lenta:

```bash
# En pom.xml, asegurar que container-build=true está configurado
# Luego:
mvn clean package -Pnative -DskipTests -Dquarkus.native.container-build=true
```

---

## 🐳 Construir Imagen Docker

### Con Binario Precompilado (Recomendado)

```bash
# Requiere que ya hayas compilado el binario nativo
docker build -f Dockerfile.native -t quarkus-recipes:native .

# Tiempo: ~20 segundos (muy rápido, solo copia el binario)
```

### Dockerfile.native Explicado

```dockerfile
# Stage 1: Placeholder (compilación hecha localmente)
FROM quay.io/quarkus/ubi9-quarkus-micro-image:2.0 AS builder

# Stage 2: Runtime (imagen final minimal)
FROM quay.io/quarkus/ubi9-quarkus-micro-image:2.0

WORKDIR /work

# Copiar el binario compilado
COPY bootstrap/target/*-runner /work/application

USER 1001
EXPOSE 8080
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
```

---

## ▶️ Ejecutar la Imagen Nativa

### Opción 1: Docker Directo

```bash
docker run -d \
  --name quarkus-recipes-native \
  -p 8080:8080 \
  -e QUARKUS_DATASOURCE_JDBC_URL="jdbc:postgresql://your-host/db" \
  -e QUARKUS_DATASOURCE_USERNAME="user" \
  -e QUARKUS_DATASOURCE_PASSWORD="pass" \
  quarkus-recipes:native

# Verificar que inició rápido
curl http://localhost:8080/health
```

### Opción 2: Docker Compose (Recomendado)

```bash
docker compose -f docker-compose.native.yml up -d

# Logs en vivo
docker compose -f docker-compose.native.yml logs -f quarkus-native-app
```

**Archivo:** `docker-compose.native.yml`

---

## 📝 Variables de Entorno Importantes

```bash
# Database
QUARKUS_DATASOURCE_DB_KIND=postgresql
QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://host:5432/db
QUARKUS_DATASOURCE_USERNAME=user
QUARKUS_DATASOURCE_PASSWORD=pass

# HTTP
QUARKUS_HTTP_HOST=0.0.0.0
QUARKUS_HTTP_PORT=8080

# Logging
QUARKUS_LOG_LEVEL=INFO

# Security JWT
APP_SECURITY_JWT_SECRET=your-secret-base64
APP_SECURITY_JWT_EXPIRATION_MINUTES=30

# CORS
QUARKUS_HTTP_CORS=true
QUARKUS_HTTP_CORS_ORIGINS=*
```

---

## ✅ Verificar Que Funciona

```bash
# 1. Health check
curl http://localhost:8080/health
# Output: {"status":"UP","checks":[...]}

# 2. API Recipes
curl http://localhost:8080/api/recipes
# Output: [{"id":1,"nombre":"..."}]

# 3. Swagger UI
curl http://localhost:8080/swagger-ui
# Output: HTML con interfaz Swagger

# 4. OpenAPI JSON
curl http://localhost:8080/q/openapi.json
# Output: {"openapi":"3.1.0",...}
```

---

## 🔧 Troubleshooting

### Error: "mvn: command not found"
**Solución:** Instalar Maven 3.9+ como se describió arriba.

### Error: "No container CLI was found"
**Solución:** Instalar Docker.

### Error: "Unable to provision" en Maven
**Solución:** Actualizar Maven a 3.9+, eliminar cache:
```bash
rm -rf ~/.m2/repository
mvn clean package -Pnative -DskipTests -Dquarkus.native.container-build=true
```

### Aplicación no inicia (Database errors)
**Solución:** Verificar credenciales y que la BD es accesible:
```bash
# Probar conexión
psql postgresql://user:pass@host:5432/db
```

### Imagen nativa muy grande (>300 MB)
**Normal.** GraalVM incluye dependencias del runtime. Para producción, considerar usar UBI Micro en lugar de UBI 9.

---

## 📈 Optimizaciones Avanzadas

### 1. Reducir Tamaño con UBI Distroless

```dockerfile
FROM quay.io/quarkus/ubi9-quarkus-distroless-image:2.0
# Tamaño: ~50 MB (vs 282 MB)
# Trade-off: No shell, no debugging
```

### 2. Build Cache Efectivo

```bash
# En pom.xml:
<quarkus.native.container-build>true</quarkus.native.container-build>
```

Las primeras compilaciones son lentas, pero Maven cachea dependencias.

### 3. Native Image Hints

Para reflexión dinámica (si es necesario):

```java
@RegisterForReflection(targets = { MyClass.class })
public class MyReflectionHints { }
```

---

## 🚀 Deploy a Producción

### Con Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: quarkus-recipes
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: quarkus-app
        image: quarkus-recipes:native
        resources:
          limits:
            memory: "128Mi"
            cpu: "100m"
          requests:
            memory: "64Mi"
            cpu: "50m"
        livenessProbe:
          httpGet:
            path: /health/live
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 10
```

### Con Docker Swarm

```bash
docker service create \
  --name quarkus-recipes \
  --replicas 3 \
  -p 8080:8080 \
  -e QUARKUS_DATASOURCE_JDBC_URL="..." \
  quarkus-recipes:native
```

---

## 📚 Referencias

- [Quarkus Native Image Guide](https://quarkus.io/guides/building-native-image)
- [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/)
- [Mandrel (GraalVM for Quarkus)](https://github.com/graalvm/mandrel)
- [UBI Images (Red Hat)](https://www.redhat.com/en/blog/introducing-red-hat-universal-base-image)

---

## ✨ Conclusión

Con **Quarkus Native**, conseguimos:

✅ **Startup ultra-rápido** (<3s vs 10-15s)
✅ **Consumo mínimo de RAM** (~44MB vs 200+MB)
✅ **Imágenes Docker más pequeñas** (14% reduction)
✅ **Zero JVM overhead**
✅ **Ideal para Serverless, Kubernetes, Edge**

**¡Listo para producción! 🚀**
