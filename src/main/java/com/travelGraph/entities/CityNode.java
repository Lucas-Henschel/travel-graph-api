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
import java.util.ArrayList;
import java.util.List;

@Node("City")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityNode {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "O nome da cidade não pode estar vazio")
    @Size(max = 100, message = "O nome pode ter no máximo 100 caracteres")
    private String name;

    private Double latitude;
    private Double longitude;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @Relationship(type = "CONECTA", direction = Relationship.Direction.OUTGOING)
    private List<CityConnection> connections = new ArrayList<>();
}
