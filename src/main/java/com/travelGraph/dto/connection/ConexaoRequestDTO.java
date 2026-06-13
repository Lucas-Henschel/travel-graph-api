package com.travelGraph.dto.connection;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConexaoRequestDTO {
    @NotNull(message = "O ID da cidade de origem é obrigatório")
    private Long cidadeOrigemId;

    @NotNull(message = "O ID da cidade de destino é obrigatório")
    private Long cidadeDestinoId;

    @NotNull(message = "A distância é obrigatória")
    private Double distancia;

    @NotNull(message = "O tempo é obrigatório")
    private Double tempo;
}

