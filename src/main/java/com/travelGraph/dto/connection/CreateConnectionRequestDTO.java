package com.travelGraph.dto.connection;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateConnectionRequestDTO {
    @NotNull(message = "O ID da origem é obrigatório")
    private Long sourceId;

    @NotNull(message = "O ID do destino é obrigatório")
    private Long targetId;

    private Double distanceKm;
    private Integer durationMinutes;
    private String transportType;

    @NotNull(message = "O tipo de conexão é obrigatório (CITY ou ATTRACTION)")
    private String connectionType;
}

