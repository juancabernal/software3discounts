package com.co.eatupapi.services.inventory.categories;

import com.co.eatupapi.domain.inventory.categories.CategoryDomain;
import com.co.eatupapi.domain.inventory.categories.CategoryStatus;
import com.co.eatupapi.dto.inventory.categories.CategoryDTO;
import com.co.eatupapi.repositories.inventory.categories.CategoryRepository;
import com.co.eatupapi.utils.inventory.categories.exceptions.ResourceNotFoundException;
import com.co.eatupapi.utils.inventory.categories.exceptions.ValidationException;
import com.co.eatupapi.utils.inventory.categories.mapper.CategoryMapper;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final List<CategoryDomain> categories = new ArrayList<>();

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @PostConstruct
    public void initData() {
        categories.clear();
        categories.addAll(categoryRepository.loadInitialCategories());
    }

    public CategoryDTO createCategory(CategoryDTO request, String username) {
        validateCategoryPayload(request);

        CategoryDomain categoryDomain = categoryMapper.toDomain(request);
        categoryDomain.setId(UUID.randomUUID().toString());
        categoryDomain.setStatus(CategoryStatus.ACTIVE);
        categoryDomain.setCreatedBy(username);
        categoryDomain.setCreatedDate(LocalDateTime.now());
        categoryDomain.setModifiedDate(LocalDateTime.now());

        categories.add(categoryDomain);
        return categoryMapper.toDto(categoryDomain);
    }

    public CategoryDTO getCategoryById(String categoryId) {
        CategoryDomain category = findCategoryById(categoryId);
        return categoryMapper.toDto(category);
    }

    public List<CategoryDTO> getCategories(String status) {
        CategoryStatus parsedStatus = parseStatus(status);

        List<CategoryDTO> result = new ArrayList<>();
        for (CategoryDomain category : categories) {
            if (parsedStatus == null || category.getStatus() == parsedStatus) {
                result.add(categoryMapper.toDto(category));
            }
        }

        return result;
    }

    public CategoryDTO updateCategory(String categoryId, CategoryDTO request) {
        validateCategoryPayload(request);

        CategoryDomain existing = findCategoryById(categoryId);
        existing.setType(request.getType());
        existing.setName(request.getName());
        existing.setBranchId(request.getBranchId());
        existing.setEntryDate(request.getEntryDate());
        existing.setModifiedDate(LocalDateTime.now());

        return categoryMapper.toDto(existing);
    }

    public CategoryDTO updateStatus(String categoryId, String status) {
        CategoryStatus newStatus = parseRequiredStatus(status);

        CategoryDomain existing = findCategoryById(categoryId);
        existing.setStatus(newStatus);
        existing.setModifiedDate(LocalDateTime.now());

        return categoryMapper.toDto(existing);
    }

    private CategoryDomain findCategoryById(String categoryId) {
        for (CategoryDomain category : categories) {
            if (category.getId().equals(categoryId)) {
                return category;
            }
        }

        throw new ResourceNotFoundException("Category not found with id: " + categoryId);
    }

    private CategoryStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return CategoryStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Invalid category status value");
        }
    }

    private CategoryStatus parseRequiredStatus(String status) {
        CategoryStatus parsedStatus = parseStatus(status);
        if (parsedStatus == null) {
            throw new ValidationException("Invalid category status value");
        }
        return parsedStatus;
    }

    private void validateCategoryPayload(CategoryDTO request) {
        validateRequiredText(request.getType(), "type");
        validateRequiredText(request.getName(), "name");
        validateRequiredObject(request.getBranchId(), "branchId");
        validateRequiredObject(request.getEntryDate(), "entryDate");
    }

    private void validateRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Field '" + fieldName + "' is required and cannot be empty");
        }
    }

    private void validateRequiredObject(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException("Field '" + fieldName + "' is required and cannot be empty");
        }
    }
}
