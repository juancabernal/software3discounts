package com.descuentos.descuentos_mio.repository;

import com.descuentos.descuentos_mio.domain.DiscountsDomain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DiscountsRepository {

    Flux<DiscountsDomain> findAll();

    Mono<DiscountsDomain> findById(UUID id);

    Mono<DiscountsDomain> save(DiscountsDomain discount);

    Mono<DiscountsDomain> update(UUID id, DiscountsDomain discount);
}
