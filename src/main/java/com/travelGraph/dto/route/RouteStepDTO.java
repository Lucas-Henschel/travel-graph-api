package com.travelGraph.dto.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteStepDTO {
    private Long id;
    private String name;
    private String type;
    private Double latitude;
    private Double longitude;
    private Integer order;
}
