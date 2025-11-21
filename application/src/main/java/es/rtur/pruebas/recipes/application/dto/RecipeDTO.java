package es.rtur.pruebas.recipes.application.dto;

import es.rtur.pruebas.recipes.domain.entity.DifficultyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDTO {

    private String id;

    @NotBlank(message = "Recipe name is required")
    private String name;

    private String description;

    @NotNull(message = "Preparation time is required")
    @Positive(message = "Preparation time must be positive")
    private Integer preparationTimeMinutes;

    @NotNull(message = "Difficulty level is required")
    private DifficultyLevel difficulty;

    private List<String> ingredients;
    private List<String> instructions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
