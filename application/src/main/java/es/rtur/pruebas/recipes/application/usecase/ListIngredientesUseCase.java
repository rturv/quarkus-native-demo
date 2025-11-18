package es.rtur.pruebas.recipes.application.usecase;

import es.rtur.pruebas.recipes.application.dto.IngredienteDTO;
import es.rtur.pruebas.recipes.domain.entity.Ingrediente;
import es.rtur.pruebas.recipes.domain.repository.IngredienteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use Case for listing all ingredientes (RF-06).
 */
@ApplicationScoped
public class ListIngredientesUseCase {

    private final IngredienteRepository ingredienteRepository;

    @Inject
    public ListIngredientesUseCase(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    /**
     * Lists all ingredientes.
     * @return List of IngredienteDTO
     */
    public List<IngredienteDTO> execute() {
        return ingredienteRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private IngredienteDTO mapToDTO(Ingrediente ingrediente) {
        return new IngredienteDTO(
                ingrediente.getId() != null ? ingrediente.getId().getValue() : null,
                ingrediente.getNombre(),
                ingrediente.getTipo(),
                ingrediente.getFCreacion(),
                ingrediente.getFModificacion()
        );
    }
}
