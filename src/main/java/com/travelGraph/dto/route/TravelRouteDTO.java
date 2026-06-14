package com.travelGraph.dto.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelRouteDTO {
    private List<CityRouteDTO> cities;
    private Double totalDistance;
    private Double totalTime;
}
