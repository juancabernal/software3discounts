package com.descuentos.descuentos_mio.controllers;

import com.descuentos.descuentos_mio.dto.DiscountsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

@SpringBootTest
@AutoConfigureWebTestClient
class DiscountsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldGetDiscountsList() {
        webTestClient.get()
                .uri("/comercial/api/v1/discounts")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DiscountsDto.class)
                .hasSize(2);
    }

    @Test
    void shouldCreateDiscount() {
        DiscountsDto request = new DiscountsDto(
                null,
                UUID.randomUUID(),
                18,
                "Descuento temporal",
                true
        );

        webTestClient.post()
                .uri("/comercial/api/v1/discounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.percentage").isEqualTo(18);
    }
}
