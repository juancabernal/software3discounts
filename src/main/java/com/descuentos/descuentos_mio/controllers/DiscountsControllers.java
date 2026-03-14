package com.descuentos.descuentos_mio.controllers;

import com.descuentos.descuentos_mio.dto.DiscountsDto;
import com.descuentos.descuentos_mio.service.DiscountsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/comercial/api/v1/discounts")
public class DiscountsController {

    private final DiscountsService discountsService;

    public DiscountsController(DiscountsService discountsService) {
        this.discountsService = discountsService;
    }

    @GetMapping
    public Flux<DiscountsDto> getAllDiscounts() {
        return discountsService.getAllDiscounts();
    }

    @GetMapping("/{discountId}")
    public Mono<ResponseEntity<DiscountsDto>> getDiscountById(@PathVariable UUID discountId) {
        return discountsService.getDiscountById(discountId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<DiscountsDto>> createDiscount(@RequestBody DiscountsDto discountsDto) {
        return discountsService.createDiscount(discountsDto)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    @PutMapping("/{discountId}")
    public Mono<ResponseEntity<DiscountsDto>> updateDiscount(
            @PathVariable UUID discountId,
            @RequestBody DiscountsDto discountsDto
    ) {
        return discountsService.updateDiscount(discountId, discountsDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }
}
