package com.co.eatupapi.dto.inventory.transfer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferRequestDTO(
        @NotBlank(message = "La sede de origen es obligatoria")
        String sedeOrigen,

        @NotBlank(message = "La sede de destino es obligatoria")
        String sedeDestino,

        @NotNull(message = "La fecha de envio es obligatoria")
        LocalDateTime fechaEnvio,

        @NotNull(message = "La fecha de llegada es obligatoria")
        LocalDateTime fechaLlegada,

        @NotBlank(message = "El responsable es obligatorio")
        String responsable,

        @NotNull(message = "El producto es obligatorio")
        UUID producto,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        Integer cantidad,

        String observaciones
) {
}
