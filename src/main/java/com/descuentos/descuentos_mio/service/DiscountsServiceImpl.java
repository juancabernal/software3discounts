package com.descuentos.descuentos_mio.service;

import com.descuentos.descuentos_mio.domain.DiscountsDomain;
import com.descuentos.descuentos_mio.dto.DiscountsDto;
import com.descuentos.descuentos_mio.repository.DiscountsRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class DiscountsServiceImpl implements DiscountsService {

    private final DiscountsRepository discountsRepository;

    public DiscountsServiceImpl(DiscountsRepository discountsRepository) {
        this.discountsRepository = discountsRepository;
    }

    @Override
    public Flux<DiscountsDto> getAllDiscounts() {
        return discountsRepository.findAll().map(this::toDto);
    }

    @Override
    public Mono<DiscountsDto> getDiscountById(UUID id) {
        return discountsRepository.findById(id).map(this::toDto);
    }

    @Override
    public Mono<DiscountsDto> createDiscount(DiscountsDto discount) {
        return Mono.just(discount)
                .map(this::validate)
                .map(this::toDomain)
                .flatMap(discountsRepository::save)
                .map(this::toDto);
    }

    @Override
    public Mono<DiscountsDto> updateDiscount(UUID id, DiscountsDto discount) {
        return Mono.just(discount)
                .map(this::validate)
                .map(this::toDomain)
                .flatMap(domain -> discountsRepository.update(id, domain))
                .map(this::toDto);
    }

    private DiscountsDto validate(DiscountsDto discount) {
        if (discount.getPercentage() == null || discount.getPercentage() < 0 || discount.getPercentage() > 100) {
            throw new IllegalArgumentException("percentage debe estar entre 0 y 100");
        }
        if (discount.getCategoryId() == null) {
            throw new IllegalArgumentException("categoryId es obligatorio");
        }
        if (discount.getDescription() == null || discount.getDescription().isBlank()) {
            throw new IllegalArgumentException("description es obligatoria");
        }
        if (discount.getStatus() == null) {
            discount.setStatus(Boolean.TRUE);
        }
        return discount;
    }

    private DiscountsDto toDto(DiscountsDomain domain) {
        return new DiscountsDto(
                domain.getId(),
                domain.getCategoryId(),
                domain.getPercentage(),
                domain.getDescription(),
                domain.getStatus()
        );
    }

    private DiscountsDomain toDomain(DiscountsDto dto) {
        UUID id = dto.getId();
        return new DiscountsDomain(
                id,
                dto.getCategoryId(),
                dto.getPercentage(),
                dto.getDescription(),
                dto.getStatus()
        );
    }
}
