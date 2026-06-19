package com.travelGraph.dto.route;

import com.travelGraph.dto.attraction.AttractionResponseDTO;
import com.travelGraph.entities.CityNode;
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

    public static CityRouteDTO fromCityNode(CityNode city, List<AttractionResponseDTO> attractions) {
        CityRouteDTO dto = new CityRouteDTO();
        dto.setId(city.getId());
        dto.setName(city.getName());
        dto.setLatitude(city.getLatitude());
        dto.setLongitude(city.getLongitude());
        dto.setAttractions(attractions);
        return dto;
    }
}
