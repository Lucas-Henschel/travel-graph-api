package com.travelGraph.dto.connection;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionRequestDTO {
    @NotNull(message = "O ID da cidade de origem é obrigatório")
    private Long originCityId;

    @NotNull(message = "O ID da cidade de destino é obrigatório")
    private Long destinationCityId;

    @NotNull(message = "A distância é obrigatória")
    private Double distance;

    @NotNull(message = "O tempo é obrigatório")
    private Double time;
}
