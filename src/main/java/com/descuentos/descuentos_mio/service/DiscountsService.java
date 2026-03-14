package com.descuentos.descuentos_mio.service;

import com.descuentos.descuentos_mio.dto.DiscountsDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DiscountsService {

    Flux<DiscountsDto> getAllDiscounts();

    Mono<DiscountsDto> getDiscountById(UUID id);

    Mono<DiscountsDto> createDiscount(DiscountsDto discount);

    Mono<DiscountsDto> updateDiscount(UUID id, DiscountsDto discount);
}
