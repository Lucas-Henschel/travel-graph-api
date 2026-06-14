package com.travelGraph.dto.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoteiroDTO {
    private List<CidadeRotaDTO> cidades;
    private Double distanciaTotal;
    private Double tempoTotal;
}
