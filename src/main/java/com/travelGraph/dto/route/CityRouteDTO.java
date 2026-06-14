package com.travelGraph.dto.route;

import com.travelGraph.dto.attraction.AttractionResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityRouteDTO {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private List<AttractionResponseDTO> attractions;
}
