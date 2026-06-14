package com.travelGraph.dto.connection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConexaoResponseDTO {
    private Long id;
    private Long cidadeOrigemId;
    private Long cidadeDestinoId;
    private String cidadeOrigemNome;
    private String cidadeDestinoNome;
    private Double distancia;
    private Double tempo;
    private LocalDateTime createdAt;
}
