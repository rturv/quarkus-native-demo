#!/bin/bash
# Quick start script for development mode

echo "🚀 Starting Quarkus Recipes API in development mode..."
echo ""
echo "📖 Available endpoints:"
echo "   - API:        http://localhost:8080/api/recipes"
echo "   - Swagger UI: http://localhost:8080/swagger-ui"
echo "   - Health:     http://localhost:8080/health"
echo ""
echo "Press Ctrl+C to stop"
echo ""

cd bootstrap
mvn quarkus:dev
