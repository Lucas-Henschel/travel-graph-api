package com.travelGraph.controller;

import com.travelGraph.dto.connection.ConexaoRequestDTO;
import com.travelGraph.dto.connection.ConexaoResponseDTO;
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
     * GET /connections
     */
    @GetMapping
    public ResponseEntity<List<ConexaoResponseDTO>> findAll() {
        List<ConexaoResponseDTO> response = conexaoService.findAll();
        return ResponseEntity.ok().body(response);
    }

    /**
     * Busca uma conexão por ID
     * GET /connections/{id}
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<ConexaoResponseDTO> findById(
            @Valid @PathVariable Long id) {
        ConexaoResponseDTO response = conexaoService.findById(id);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Cria uma nova conexão entre cidades
     * POST /connections
     */
    @PostMapping
    public ResponseEntity<ConexaoResponseDTO> create(
            @Valid @RequestBody ConexaoRequestDTO createConexaoDTO) {
        ConexaoResponseDTO conexao = conexaoService.create(createConexaoDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(conexao.getId()).toUri();

        return ResponseEntity.created(uri).body(conexao);
    }

    /**
     * Remove uma conexão
     * DELETE /connections/{id}
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(
            @Valid @PathVariable Long id) {
        conexaoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

