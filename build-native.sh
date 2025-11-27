#!/bin/bash

# ============================================================================
# build-native.sh - Script para compilar e imagen Quarkus nativa
# ============================================================================
# Uso: ./build-native.sh [--image-only] [--compose] [--run]
#
# Opciones:
#   (sin args)      Compilar binario nativo + construir imagen Docker
#   --image-only    Solo construir imagen Docker (asume binario ya existe)
#   --compose       Lanzar docker compose después de compilar
#   --run           Ejecutar contenedor después de compilar
#   --help          Mostrar esta ayuda
# ============================================================================

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Funciones
print_header() {
    echo -e "${BLUE}============================================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}============================================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

show_help() {
    echo "Uso: $0 [OPCIÓN]"
    echo ""
    echo "Opciones:"
    echo "  (sin args)      Compilar binario nativo + construir imagen Docker"
    echo "  --image-only    Solo construir imagen Docker (asume binario ya existe)"
    echo "  --compose       Lanzar docker compose después de compilar"
    echo "  --run           Ejecutar contenedor después de compilar"
    echo "  --help          Mostrar esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  $0                    # Compilar + imagen"
    echo "  $0 --image-only       # Solo imagen"
    echo "  $0 --compose          # Compilar + imagen + compose"
    echo "  $0 --run              # Compilar + imagen + ejecutar contenedor"
}

check_maven() {
    if ! command -v mvn &> /dev/null; then
        print_error "Maven no está instalado"
        echo ""
        echo "Instalando Maven 3.9.6..."
        mkdir -p ~/maven
        cd ~/maven
        curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz | tar xz
        export PATH=~/maven/apache-maven-3.9.6/bin:$PATH
        echo 'export PATH=~/maven/apache-maven-3.9.6/bin:$PATH' >> ~/.bashrc
        print_success "Maven 3.9.6 instalado"
    fi
    
    MAVEN_VERSION=$(mvn -v 2>/dev/null | head -1 | awk '{print $3}')
    print_info "Maven version: $MAVEN_VERSION"
}

check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker no está instalado"
        exit 1
    fi
    print_success "Docker disponible"
}

compile_native() {
    print_header "Compilando Binario Nativo con GraalVM"
    
    print_info "Esta compilación puede tomar 2-3 minutos..."
    print_info "Se descargará una imagen de Docker con GraalVM/Mandrel"
    
    mvn clean package \
        -Pnative \
        -DskipTests \
        -Dquarkus.native.container-build=true \
        -ntp \
        -q
    
    BINARY_PATH="bootstrap/target/bootstrap-1.0.0-SNAPSHOT-runner"
    
    if [ -f "$BINARY_PATH" ]; then
        BINARY_SIZE=$(ls -lh "$BINARY_PATH" | awk '{print $5}')
        print_success "Binario nativo compilado: $BINARY_SIZE"
        print_info "Ubicación: $BINARY_PATH"
    else
        print_error "No se encontró el binario nativo"
        exit 1
    fi
}

build_image() {
    print_header "Construyendo Imagen Docker"
    
    docker build -f Dockerfile.native -t quarkus-recipes:native . --quiet
    
    IMAGE_SIZE=$(docker images quarkus-recipes:native --format "{{.Size}}")
    print_success "Imagen Docker construida: $IMAGE_SIZE"
}

run_compose() {
    print_header "Lanzando Docker Compose"
    
    docker compose -f docker-compose.native.yml up -d
    
    print_info "Esperando 15 segundos para inicialización..."
    sleep 15
    
    # Test health check
    if curl -s http://localhost:8080/health > /dev/null 2>&1; then
        print_success "Aplicación iniciada correctamente"
        echo ""
        echo "Endpoints disponibles:"
        echo "  - Health: http://localhost:8080/health"
        echo "  - API Recipes: http://localhost:8080/api/recipes"
        echo "  - Swagger UI: http://localhost:8080/swagger-ui"
        echo "  - OpenAPI: http://localhost:8080/q/openapi.json"
        echo ""
        echo "Para detener:"
        echo "  docker compose -f docker-compose.native.yml down"
    else
        print_error "La aplicación no respondió al health check"
        print_info "Mostrando logs:"
        docker compose -f docker-compose.native.yml logs quarkus-native-app
    fi
}

run_container() {
    print_header "Ejecutando Contenedor"
    
    # Detener si ya existe
    docker stop quarkus-recipes-native 2>/dev/null || true
    docker rm quarkus-recipes-native 2>/dev/null || true
    
    docker run -d \
        --name quarkus-recipes-native \
        -p 8080:8080 \
        -e QUARKUS_DATASOURCE_DB_KIND=postgresql \
        -e QUARKUS_DATASOURCE_USERNAME=neondb_owner \
        -e QUARKUS_DATASOURCE_PASSWORD=npg_wHpyzmq02ZDW \
        -e QUARKUS_DATASOURCE_JDBC_URL="jdbc:postgresql://ep-black-wind-a94um1q6-pooler.gwc.azure.neon.tech/neondb?sslmode=require&channel_binding=require" \
        -e QUARKUS_LOG_LEVEL=INFO \
        quarkus-recipes:native
    
    print_info "Esperando 5 segundos para inicialización..."
    sleep 5
    
    # Test health check
    if curl -s http://localhost:8080/health > /dev/null 2>&1; then
        print_success "Contenedor ejecutándose correctamente"
        echo ""
        echo "Contenedor: quarkus-recipes-native"
        echo "Puerto: 8080"
        echo ""
        echo "Para ver logs:"
        echo "  docker logs -f quarkus-recipes-native"
        echo ""
        echo "Para detener:"
        echo "  docker stop quarkus-recipes-native"
    else
        print_error "El contenedor no respondió"
        print_info "Mostrando logs:"
        docker logs quarkus-recipes-native
    fi
}

# Main
print_header "Quarkus Native Build Script"

# Parse arguments
IMAGE_ONLY=false
USE_COMPOSE=false
RUN_CONTAINER=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --image-only)
            IMAGE_ONLY=true
            shift
            ;;
        --compose)
            USE_COMPOSE=true
            shift
            ;;
        --run)
            RUN_CONTAINER=true
            shift
            ;;
        --help)
            show_help
            exit 0
            ;;
        *)
            print_error "Opción desconocida: $1"
            show_help
            exit 1
            ;;
    esac
done

# Verificaciones previas
check_docker

if [ "$IMAGE_ONLY" = false ]; then
    check_maven
    compile_native
fi

build_image

if [ "$USE_COMPOSE" = true ]; then
    run_compose
elif [ "$RUN_CONTAINER" = true ]; then
    run_container
fi

print_header "✓ ¡Completado!"
