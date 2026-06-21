package com.travelGraph.dto.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortestPathResponseDTO {
    private List<RouteStepDTO> path;
    private Double totalDistanceKm;
    private Integer totalDurationMinutes;
    private Integer stepCount;
}
