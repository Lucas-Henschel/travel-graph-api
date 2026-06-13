package com.travelGraph.dto.route;

import com.travelGraph.dto.attraction.AttractionResponseDTO;
import com.travelGraph.dto.city.CityResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearbyLocationDTO {
    private Object location;
    private String type;
    private Double distanceKm;
    private Integer durationMinutes;
    private String transportType;
}

