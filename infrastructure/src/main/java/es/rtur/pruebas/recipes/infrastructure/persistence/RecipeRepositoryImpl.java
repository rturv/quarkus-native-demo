package es.rtur.pruebas.recipes.infrastructure.persistence;

import es.rtur.pruebas.recipes.domain.entity.Recipe;
import es.rtur.pruebas.recipes.domain.repository.RecipeRepository;
import es.rtur.pruebas.recipes.domain.valueobject.RecipeId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of RecipeRepository that delegates to RecetaRepositoryImpl.
 * This is an adapter to handle the English/Spanish entity mismatch.
 */
@ApplicationScoped
public class RecipeRepositoryImpl implements RecipeRepository {

    @Inject
    RecetaRepositoryImpl recetaRepository;

    @Override
    public Recipe save(Recipe recipe) {
        // For now, throw an exception - this needs proper mapping between Recipe and Receta
        throw new UnsupportedOperationException("RecipeRepository.save() not implemented yet - needs Recipe/Receta mapping");
    }

    @Override
    public Optional<Recipe> findById(RecipeId id) {
        throw new UnsupportedOperationException("RecipeRepository.findById() not implemented yet - needs Recipe/Receta mapping");
    }

    @Override
    public List<Recipe> findAll() {
        throw new UnsupportedOperationException("RecipeRepository.findAll() not implemented yet - needs Recipe/Receta mapping");
    }

    @Override
    public List<Recipe> findByNameContaining(String name) {
        throw new UnsupportedOperationException("RecipeRepository.findByNameContaining() not implemented yet - needs Recipe/Receta mapping");
    }

    @Override
    public void deleteById(RecipeId id) {
        throw new UnsupportedOperationException("RecipeRepository.deleteById() not implemented yet - needs Recipe/Receta mapping");
    }

    @Override
    public boolean existsById(RecipeId id) {
        throw new UnsupportedOperationException("RecipeRepository.existsById() not implemented yet - needs Recipe/Receta mapping");
    }
}