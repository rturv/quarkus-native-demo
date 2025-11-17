package com.quarkus.recipes.infrastructure.rest;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Health check endpoints for the application
 */
@ApplicationScoped
public class HealthResource {

    @Liveness
    public HealthCheck livenessCheck() {
        return () -> HealthCheckResponse.up("Recipes API is alive");
    }

    @Readiness
    public HealthCheck readinessCheck() {
        return () -> HealthCheckResponse.up("Recipes API is ready");
    }
}
