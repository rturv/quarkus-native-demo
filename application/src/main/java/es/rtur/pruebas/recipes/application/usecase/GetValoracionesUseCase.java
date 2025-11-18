package es.rtur.pruebas.recipes.application.usecase;

import es.rtur.pruebas.recipes.domain.repository.ValoracionRepository;
import es.rtur.pruebas.recipes.domain.valueobject.RecetaId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Use Case for getting valoracion counts for a recipe (RF-04).
 */
@ApplicationScoped
public class GetValoracionesUseCase {

    private final ValoracionRepository valoracionRepository;

    @Inject
    public GetValoracionesUseCase(ValoracionRepository valoracionRepository) {
        this.valoracionRepository = valoracionRepository;
    }

    /**
     * Gets like and dislike counts for a recipe.
     * @param idReceta Recipe ID
     * @return ValoracionSummary with counts
     */
    public ValoracionSummary execute(Integer idReceta) {
        RecetaId recetaId = RecetaId.of(idReceta);
        
        long likes = valoracionRepository.countLikesByReceta(recetaId);
        long dislikes = valoracionRepository.countDislikesByReceta(recetaId);

        return new ValoracionSummary(likes, dislikes);
    }

    /**
     * Inner class to return valoracion summary.
     */
    public static class ValoracionSummary {
        private final long likes;
        private final long dislikes;

        public ValoracionSummary(long likes, long dislikes) {
            this.likes = likes;
            this.dislikes = dislikes;
        }

        public long getLikes() { return likes; }
        public long getDislikes() { return dislikes; }
        public long getTotal() { return likes + dislikes; }
    }
}
