package com.travelGraph.dto.city;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCityRequestDTO {
    @NotBlank(message = "O nome da cidade é obrigatório")
    @Size(max = 100, message = "O nome pode ter no máximo 100 caracteres")
    private String name;

    private Double latitude;
    private Double longitude;
}
