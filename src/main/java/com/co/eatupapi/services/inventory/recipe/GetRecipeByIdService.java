package com.co.eatupapi.services.inventory.recipe;

import com.co.eatupapi.dto.inventory.recipe.RecipeResponse;
import com.co.eatupapi.repositories.inventory.recipe.RecipeRepository;
import com.co.eatupapi.utils.inventory.recipe.exceptions.RecipeNotFoundException;
import com.co.eatupapi.utils.inventory.recipe.mapper.RecipeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetRecipeByIdService {

    private static final String RECIPE_WITH_ID_NOT_FOUND = "La receta con el id %s no existe.";

    private final RecipeRepository repo;
    private final RecipeMapper mapper;

    public GetRecipeByIdService(RecipeRepository repo, RecipeMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public RecipeResponse run(UUID id) {
        validateId(id);
        var recipe = repo.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException(
                        String.format(RECIPE_WITH_ID_NOT_FOUND, id)
                ));

        return mapper.toResponse(recipe);
    }

    private void validateId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("RECIPE_ID_REQUIRED");
        }
    }
}