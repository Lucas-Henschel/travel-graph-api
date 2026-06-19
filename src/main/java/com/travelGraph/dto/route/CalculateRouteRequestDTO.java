package com.travelGraph.dto.route;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculateRouteRequestDTO {
    @NotNull(message = "O ID da cidade de origem é obrigatório")
    private Long startCityId;

    @NotNull(message = "O ID da cidade de destino é obrigatório")
    private Long endCityId;

    private String criteria = "distance";
}
