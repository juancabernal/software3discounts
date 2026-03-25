package com.co.eatupapi.repositories.inventory.categories;

import com.co.eatupapi.domain.inventory.categories.CategoryDomain;
import com.co.eatupapi.domain.inventory.categories.CategoryStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepository {

    public List<CategoryDomain> loadInitialCategories() {
        LocalDateTime now = LocalDateTime.now();

        CategoryDomain category1 = new CategoryDomain();
        category1.setId("category-001");
        category1.setType("Alimentos");
        category1.setName("Frutas");
        category1.setBranchId(1L);
        category1.setCreatedBy("admin");
        category1.setEntryDate(LocalDate.now().minusDays(7));
        category1.setStatus(CategoryStatus.ACTIVE);
        category1.setCreatedDate(now.minusDays(7));
        category1.setModifiedDate(now.minusDays(1));

        CategoryDomain category2 = new CategoryDomain();
        category2.setId("category-002");
        category2.setType("Bebidas");
        category2.setName("Gaseosas");
        category2.setBranchId(2L);
        category2.setCreatedBy("admin");
        category2.setEntryDate(LocalDate.now().minusDays(10));
        category2.setStatus(CategoryStatus.ACTIVE);
        category2.setCreatedDate(now.minusDays(10));
        category2.setModifiedDate(now.minusDays(2));

        CategoryDomain category3 = new CategoryDomain();
        category3.setId("category-003");
        category3.setType("Aseo");
        category3.setName("Limpieza");
        category3.setBranchId(3L);
        category3.setCreatedBy("admin");
        category3.setEntryDate(LocalDate.now().minusDays(15));
        category3.setStatus(CategoryStatus.INACTIVE);
        category3.setCreatedDate(now.minusDays(15));
        category3.setModifiedDate(now.minusDays(3));

        return List.of(category1, category2, category3);
    }
}

