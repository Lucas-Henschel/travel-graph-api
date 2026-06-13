package com.travelGraph.controller;

import com.travelGraph.dto.connection.ConexaoRequestDTO;
import com.travelGraph.dto.connection.ConexaoResponseDTO;
import com.travelGraph.services.ConexaoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/conexoes")
public class ConexaoController {
    @Autowired
    private ConexaoService conexaoService;

    @GetMapping
    public ResponseEntity<List<ConexaoResponseDTO>> findAll() {
        log.info("GET /conexoes - Finding all conexoes");
        List<ConexaoResponseDTO> response = conexaoService.findAll();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ConexaoResponseDTO> findById(@Valid @PathVariable Long id) {
        log.info("GET /conexoes/{} - Finding conexao by id", id);
        ConexaoResponseDTO response = conexaoService.findById(id);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<ConexaoResponseDTO> create(@Valid @RequestBody ConexaoRequestDTO createConexaoDTO) {
        log.info("POST /conexoes - Creating new conexao from city {} to city {}", 
            createConexaoDTO.getCidadeOrigemId(), createConexaoDTO.getCidadeDestinoId());
        ConexaoResponseDTO conexao = conexaoService.create(createConexaoDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(conexao.getId()).toUri();

        return ResponseEntity.created(uri).body(conexao);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@Valid @PathVariable Long id) {
        log.info("DELETE /conexoes/{} - Deleting conexao", id);
        conexaoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

