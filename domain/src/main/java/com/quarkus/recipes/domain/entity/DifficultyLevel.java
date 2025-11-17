package com.quarkus.recipes.domain.entity;

/**
 * Domain Value Object: DifficultyLevel
 * Represents the difficulty level of a recipe
 */
public enum DifficultyLevel {
    EASY("Easy - Suitable for beginners"),
    MEDIUM("Medium - Some cooking experience required"),
    HARD("Hard - Advanced cooking skills needed"),
    EXPERT("Expert - Professional level");

    private final String description;

    DifficultyLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
