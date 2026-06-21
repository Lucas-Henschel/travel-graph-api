package com.travelGraph.dto.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryDTO {
    private String title;
    private String description;
    private List<RouteStepDTO> stops;
    private Double totalDistanceKm;
    private Integer totalDurationMinutes;
    private Double rating;
}
