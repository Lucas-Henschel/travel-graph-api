package com.travelGraph.entities;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "O nome não pode estar vazio")
    @Size(max = 45, message = "O nome pode ter no máximo 45 caracteres")
    private String name;

    @NotBlank(message = "O e-mail não pode estar vazio")
    @Size(max = 45, message = "O e-mail pode ter no máximo 45 caracteres")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    private String password;

    private LocalDateTime createdAt = LocalDateTime.now();
}
