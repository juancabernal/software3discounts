package com.descuentos.descuentos_mio.domain;

import java.util.UUID;

public class DiscountsDomain {

    private UUID id;
    private UUID categoryId;
    private Integer percentage;
    private String description;
    private Boolean status;

    public DiscountsDomain() {
    }

    public DiscountsDomain(UUID id, UUID categoryId, Integer percentage, String description, Boolean status) {
        this.id = id;
        this.categoryId = categoryId;
        this.percentage = percentage;
        this.description = description;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
