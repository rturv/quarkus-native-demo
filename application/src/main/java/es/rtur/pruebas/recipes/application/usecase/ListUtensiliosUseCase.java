package es.rtur.pruebas.recipes.application.usecase;

import es.rtur.pruebas.recipes.application.dto.UtensilioDTO;
import es.rtur.pruebas.recipes.domain.entity.Utensilio;
import es.rtur.pruebas.recipes.domain.repository.UtensilioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use Case for listing all utensilios (RF-06).
 */
@ApplicationScoped
public class ListUtensiliosUseCase {

    private final UtensilioRepository utensilioRepository;

    @Inject
    public ListUtensiliosUseCase(UtensilioRepository utensilioRepository) {
        this.utensilioRepository = utensilioRepository;
    }

    /**
     * Lists all utensilios.
     * @return List of UtensilioDTO
     */
    public List<UtensilioDTO> execute() {
        return utensilioRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private UtensilioDTO mapToDTO(Utensilio utensilio) {
        return new UtensilioDTO(
                utensilio.getId() != null ? utensilio.getId().getValue() : null,
                utensilio.getNombre(),
                utensilio.getTipo(),
                utensilio.getFCreacion(),
                utensilio.getFModificacion()
        );
    }
}
