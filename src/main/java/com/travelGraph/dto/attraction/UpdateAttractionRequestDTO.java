package com.travelGraph.dto.attraction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAttractionRequestDTO {
    @NotBlank(message = "O nome do ponto turístico é obrigatório")
    @Size(max = 100, message = "O nome pode ter no máximo 100 caracteres")
    private String name;

    @Size(max = 255, message = "A descrição pode ter no máximo 255 caracteres")
    private String description;

    @Size(max = 50, message = "A categoria pode ter no máximo 50 caracteres")
    private String category;

    private Double latitude;
    private Double longitude;
}
