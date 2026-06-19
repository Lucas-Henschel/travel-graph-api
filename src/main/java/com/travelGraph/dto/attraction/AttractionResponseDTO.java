package com.travelGraph.dto.attraction;

import com.travelGraph.dto.city.CityResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttractionResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CityResponseDTO city;
}
