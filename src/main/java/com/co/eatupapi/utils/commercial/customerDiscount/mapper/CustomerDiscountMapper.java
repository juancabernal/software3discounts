package com.co.eatupapi.utils.commercial.customerDiscount.mapper;

import com.co.eatupapi.domain.commercial.customerDiscount.CustomerDiscountDomain;
import com.co.eatupapi.dto.commercial.customerDiscount.CustomerDiscountDTO;
import org.springframework.stereotype.Component;

@Component
public class CustomerDiscountMapper {

    public CustomerDiscountDTO toDto(CustomerDiscountDomain domain) {
        return new CustomerDiscountDTO(
                domain.getId(),
                domain.getLocationId(),
                domain.getCustomerId(),
                domain.getDiscountId(),
                domain.getAssignedAt()
        );
    }

    public CustomerDiscountDomain toDomain(CustomerDiscountDTO dto) {
        return new CustomerDiscountDomain(
                dto.getId(),
                dto.getLocationId(),
                dto.getCustomerId(),
                dto.getDiscountId(),
                dto.getAssignedAt()
        );
    }

    public void updateDomain(CustomerDiscountDomain domain, CustomerDiscountDTO dto) {
        domain.setLocationId(dto.getLocationId());
        domain.setCustomerId(dto.getCustomerId());
        domain.setDiscountId(dto.getDiscountId());
        domain.setAssignedAt(dto.getAssignedAt());
    }
}
