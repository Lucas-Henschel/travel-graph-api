package com.travelGraph.controller;

import com.travelGraph.dto.connection.ConnectionRequestDTO;
import com.travelGraph.dto.connection.ConnectionResponseDTO;
import com.travelGraph.services.ConexaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/connections")
public class ConexaoController {
    @Autowired
    private ConexaoService conexaoService;

    /**
     * Lista todas as conexões
     */
    @GetMapping
    public ResponseEntity<List<ConnectionResponseDTO>> findAll() {
        List<ConnectionResponseDTO> response = conexaoService.findAll();
        return ResponseEntity.ok().body(response);
    }

    /**
     * Busca uma conexão por ID
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<ConnectionResponseDTO> findById(@Valid @PathVariable Long id) {
        ConnectionResponseDTO response = conexaoService.findById(id);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Cria uma nova conexão entre cidades
     */
    @PostMapping
    public ResponseEntity<ConnectionResponseDTO> create(@Valid @RequestBody ConnectionRequestDTO createConnectionDTO) {
        ConnectionResponseDTO connection = conexaoService.create(createConnectionDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(connection.getId()).toUri();

        return ResponseEntity.created(uri).body(connection);
    }

    /**
     * Atualiza uma conexão existente
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<ConnectionResponseDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody ConnectionRequestDTO updateConnectionDTO
    ) {
        ConnectionResponseDTO response = conexaoService.update(id, updateConnectionDTO);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Remove uma conexão
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@Valid @PathVariable Long id) {
        conexaoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
