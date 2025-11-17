package es.rtur.pruebas.recipes.domain.entity;

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
