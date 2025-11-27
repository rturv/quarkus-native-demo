# 📊 Comparativa de Rendimiento: JDK vs Native Image

**Fecha:** Thu Nov 27 19:34:22 CET 2025
**Proyecto:** Quarkus Recipes API
**Entorno:** Docker (WSL2/Linux)
**Base de Datos:** PostgreSQL (Neon Cloud - Real)

## 1. Resumen Ejecutivo

Esta comparativa evalúa el rendimiento de la aplicación ejecutándose en una JVM tradicional (OpenJDK 21) frente a su versión compilada nativamente con GraalVM.

| Métrica | JDK (JIT) | Native (AOT) | Diferencia |
|---------|-----------|--------------|------------|
| **Tiempo de Construcción** | 66s | 445s | Native es mucho más lento |
| **Tamaño de Imagen** | 323MB | 282MB | Native es más ligero |
| **Tiempo de Inicio** | 4.764s | 0.909s | **Native es 5x más rápido** |
| **Memoria (Idle)** | 196.6 MB | 18.5 MB | **Native consume ~10x menos** |
| **Memoria (Carga)** | 330.5 MB | 74.2 MB | Native se mantiene muy estable |

---

## 2. Metodología de Pruebas

### Entorno
- **Hardware:** Host local
- **Base de Datos:** Instancia real PostgreSQL en Neon Cloud (latencia de red real incluida).
- **Herramienta de Carga:** Script Python multihilo (http.client).

### Escenario de Prueba
1. **Arranque en frío:** Se inicia el contenedor y se mide el tiempo hasta que el health check responde `UP`.
2. **Medición Idle:** Se mide el consumo de memoria RAM (RSS) tras 2 segundos de inactividad.
3. **Prueba de Carga:**
   - **Total Requests:** 2000
   - **Concurrencia:** 20 hilos simultáneos
   - **Endpoint:** `GET /api/recipes` (consulta a BD)
4. **Medición Post-Carga:** Se mide la memoria tras finalizar la carga.

---

## 3. Resultados Detallados

### 3.1 Tiempos de Construcción (Build Time)

- **JDK:** `66 segundos`
  - Compilación estándar Maven + `docker build`.
  - Rápido, ideal para desarrollo iterativo.

- **Native:** `445 segundos` (7.4 minutos)
  - Compilación AOT (Ahead-of-Time) con GraalVM.
  - Requiere análisis estático exhaustivo de todo el código y dependencias.
  - Costoso en CPU y tiempo, ideal para pipelines de CI/CD release.

### 3.2 Tamaño de Imagen (Disk Footprint)

- **JDK:** `323MB`
  - Incluye: OS base + JVM completa + JARs de la app + Librerías.
  
- **Native:** `282MB`
  - Incluye: OS base minimal (UBI Micro) + Binario ejecutable.
  - No hay JVM, ni JARs.

### 3.3 Rendimiento en Runtime

#### Startup Time
El tiempo desde `docker run` hasta que la aplicación está lista para recibir tráfico.
- **JDK:** 4.764s
- **Native:** 0.909s
- **Conclusión:** Native es ideal para entornos Serverless (Knative, Lambda) o escalado automático en Kubernetes.

*Nota: Se optimizó la configuración (`quarkus.hibernate-orm.database.generation=none`) para evitar validaciones de esquema costosas contra la BD remota al inicio.*

#### Consumo de Memoria (RAM)
- **JDK Idle:** 196.6 MB
- **Native Idle:** 18.5 MB
- **Ahorro:** Native permite ejecutar **muchas más réplicas** con el mismo hardware.

#### Latencia y Throughput (Bajo Carga)
*Nota: Estas métricas incluyen la latencia de red hacia la BD en la nube.*

| Métrica | JDK | Native |
|---------|-----|--------|
| **Throughput (req/sec)** | 140.58612678719064 | 134.68894114327293 |
| **Latencia Promedio** | 119.08874630334958 ms | 130.1706197484587 ms |
| **Latencia P99** | 1087.5433158874512 ms | 848.0824184417725 ms |

**Observación:** En aplicaciones I/O bound (como esta, que depende de BD), el throughput suele ser similar porque el cuello de botella es la base de datos o la red, no la CPU. Sin embargo, Native logra esto con una fracción de los recursos.

---

## 4. Conclusión Final

**Usa JDK (JVM Mode) cuando:**
- Estás en fase de desarrollo (hot reload).
- Necesitas herramientas de diagnóstico dinámico (Java Agents, JMX).
- El tiempo de compilación es crítico.

**Usa Native Image cuando:**
- Despliegas en Kubernetes/Cloud y pagas por memoria/CPU.
- Necesitas escalado horizontal rápido (scale-to-zero).
- Quieres reducir la superficie de ataque (imágenes minimalistas).
- Buscas optimizar costos de infraestructura.

