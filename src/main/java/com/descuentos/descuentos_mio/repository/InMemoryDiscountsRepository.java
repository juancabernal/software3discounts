package com.descuentos.descuentos_mio.repository;

import com.descuentos.descuentos_mio.domain.DiscountsDomain;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDiscountsRepository implements DiscountsRepository {

    private final Map<UUID, DiscountsDomain> storage = new ConcurrentHashMap<>();

    public InMemoryDiscountsRepository() {
        DiscountsDomain first = new DiscountsDomain(
                UUID.randomUUID(),
                UUID.randomUUID(),
                10,
                "Descuento de bienvenida",
                true
        );

        DiscountsDomain second = new DiscountsDomain(
                UUID.randomUUID(),
                UUID.randomUUID(),
                25,
                "Descuento premium",
                true
        );

        storage.put(first.getId(), first);
        storage.put(second.getId(), second);
    }

    @Override
    public Flux<DiscountsDomain> findAll() {
        return Flux.fromIterable(storage.values());
    }

    @Override
    public Mono<DiscountsDomain> findById(UUID id) {
        return Mono.justOrEmpty(storage.get(id));
    }

    @Override
    public Mono<DiscountsDomain> save(DiscountsDomain discount) {
        UUID id = discount.getId() != null ? discount.getId() : UUID.randomUUID();
        discount.setId(id);
        storage.put(id, discount);
        return Mono.just(discount);
    }

    @Override
    public Mono<DiscountsDomain> update(UUID id, DiscountsDomain discount) {
        if (!storage.containsKey(id)) {
            return Mono.empty();
        }
        discount.setId(id);
        storage.put(id, discount);
        return Mono.just(discount);
    }
}
