package com.travelGraph.controller;

import com.travelGraph.dto.attraction.AttractionResponseDTO;
import com.travelGraph.dto.attraction.CreateAttractionRequestDTO;
import com.travelGraph.dto.attraction.UpdateAttractionRequestDTO;
import com.travelGraph.entities.AttractionNode;
import com.travelGraph.mapper.AttractionMapper;
import com.travelGraph.services.AttractionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/pontos")
public class AttractionController {
    @Autowired
    private AttractionService attractionService;

    @GetMapping
    public ResponseEntity<List<AttractionResponseDTO>> findAll(
            @RequestParam(value = "cidadeId", required = false) Long cidadeId) {
        log.info("GET /pontos - Finding attractions");
        List<AttractionNode> attractionNodes;
        
        if (cidadeId != null) {
            log.info("GET /pontos?cidadeId={} - Finding attractions by city", cidadeId);
            attractionNodes = attractionService.findByCityId(cidadeId);
        } else {
            attractionNodes = attractionService.findAll();
        }
        
        List<AttractionResponseDTO> response = new ArrayList<>();
        for (AttractionNode attractionNode : attractionNodes) {
            response.add(AttractionMapper.toDTO(attractionNode));
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AttractionResponseDTO> findById(@Valid @PathVariable Long id) {
        log.info("GET /pontos/{} - Finding attraction by id", id);
        AttractionNode attractionNode = attractionService.findById(id);
        AttractionResponseDTO response = AttractionMapper.toDTO(attractionNode);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<AttractionResponseDTO> create(@Valid @RequestBody CreateAttractionRequestDTO createAttractionDTO) {
        log.info("POST /pontos - Creating new attraction: {}", createAttractionDTO.getName());
        AttractionNode attractionNode = attractionService.create(createAttractionDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(attractionNode.getId()).toUri();

        AttractionResponseDTO response = AttractionMapper.toDTO(attractionNode);
        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<AttractionResponseDTO> update(@Valid @PathVariable Long id, @Valid @RequestBody UpdateAttractionRequestDTO updateAttractionDTO) {
        log.info("PUT /pontos/{} - Updating attraction", id);
        AttractionNode attractionNode = attractionService.update(id, updateAttractionDTO);
        AttractionResponseDTO response = AttractionMapper.toDTO(attractionNode);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@Valid @PathVariable Long id) {
        log.info("DELETE /pontos/{} - Deleting attraction", id);
        attractionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


