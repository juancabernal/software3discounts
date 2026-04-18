package com.co.eatupapi.services.inventory.recipe;

import com.co.eatupapi.domain.inventory.recipe.RecipeDomain;
import com.co.eatupapi.repositories.inventory.recipe.RecipeRepository;
import com.co.eatupapi.utils.inventory.recipe.exceptions.ErrorCode;
import com.co.eatupapi.utils.inventory.recipe.exceptions.RecipeBusinessException;
import com.co.eatupapi.utils.inventory.recipe.exceptions.RecipeNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecipeExistenceValidatorService {

    private static final String RECIPES_NOT_FOUND = "Algunas recetas no existen: %s";
    private static final String RECIPE_IDS_REQUIRED = "La lista de ids de recetas es obligatoria.";

    private final RecipeRepository repo;

    public RecipeExistenceValidatorService(RecipeRepository repo) {
        this.repo = repo;
    }

    public void run(List<UUID> ids) {
        validateIdsRequired(ids);
        validateAllExist(ids);
    }

    private void validateAllExist(List<UUID> ids) {
        var missingIds = findMissingIds(ids);

        if (!missingIds.isEmpty()) {
            throw new RecipeNotFoundException(
                    String.format(RECIPES_NOT_FOUND, missingIds)
            );
        }
    }

    private List<UUID> findMissingIds(List<UUID> ids) {
        var existingIds = repo.findAllById(ids)
                .stream()
                .map(RecipeDomain::getId)
                .collect(Collectors.toSet());

        return ids.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();
    }

    private void validateIdsRequired(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RecipeBusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    RECIPE_IDS_REQUIRED
            );
        }
    }
}