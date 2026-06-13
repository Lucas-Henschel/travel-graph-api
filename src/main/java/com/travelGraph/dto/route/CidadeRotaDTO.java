package com.travelGraph.dto.route;

import com.travelGraph.dto.attraction.AttractionResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CidadeRotaDTO {
    private Long id;
    private String nome;
    private Double latitude;
    private Double longitude;
    private List<AttractionResponseDTO> pontosTuristicos;
}

