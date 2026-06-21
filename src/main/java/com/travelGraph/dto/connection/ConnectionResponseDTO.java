package com.travelGraph.dto.connection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionResponseDTO {
    private Long id;
    private Long originCityId;
    private Long destinationCityId;
    private String originCityName;
    private String destinationCityName;
    private Double distance;
    private Double time;
    private LocalDateTime createdAt;
}
