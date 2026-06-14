package com.travelGraph.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;

@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttractionConnection {
    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private AttractionNode targetAttraction;

    private Double distanceKm;
    private Integer durationMinutes;
    private String transportType;

    private LocalDateTime createdAt = LocalDateTime.now();
}
