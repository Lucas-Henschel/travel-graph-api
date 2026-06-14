package com.travelGraph.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;

@Node("Attraction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttractionNode {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "O nome do ponto turístico não pode estar vazio")
    @Size(max = 100, message = "O nome pode ter no máximo 100 caracteres")
    private String name;

    @Size(max = 255, message = "A descrição pode ter no máximo 255 caracteres")
    private String description;

    @Size(max = 50, message = "A categoria pode ter no máximo 50 caracteres")
    private String category;

    private Double latitude;
    private Double longitude;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @Relationship(type = "PERTENCE_A", direction = Relationship.Direction.OUTGOING)
    private CityNode city;
}


