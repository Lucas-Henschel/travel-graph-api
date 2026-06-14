package com.travelGraph.controller;

import com.travelGraph.dto.attraction.AttractionResponseDTO;
import com.travelGraph.dto.attraction.CreateAttractionRequestDTO;
import com.travelGraph.dto.attraction.UpdateAttractionRequestDTO;
import com.travelGraph.entities.AttractionNode;
import com.travelGraph.mapper.AttractionMapper;
import com.travelGraph.services.AttractionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/attractions")
public class AttractionController {
    @Autowired
    private AttractionService attractionService;

    @GetMapping
    public ResponseEntity<List<AttractionResponseDTO>> findAll() {
        List<AttractionNode> attractionNodes = attractionService.findAll();

        List<AttractionResponseDTO> response = new ArrayList<>();
        for (AttractionNode attractionNode : attractionNodes) {
            response.add(AttractionMapper.toDTO(attractionNode));
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AttractionResponseDTO> findById(
            @Valid @PathVariable Long id) {
        AttractionNode attractionNode = attractionService.findById(id);
        AttractionResponseDTO response = AttractionMapper.toDTO(attractionNode);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<AttractionResponseDTO> create(
            @Valid @RequestBody CreateAttractionRequestDTO createAttractionDTO) {
        AttractionNode attractionNode = attractionService.create(createAttractionDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(attractionNode.getId()).toUri();

        AttractionResponseDTO response = AttractionMapper.toDTO(attractionNode);
        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<AttractionResponseDTO> update(
            @Valid @PathVariable Long id,
            @Valid @RequestBody UpdateAttractionRequestDTO updateAttractionDTO) {
        AttractionNode attractionNode = attractionService.update(id, updateAttractionDTO);
        AttractionResponseDTO response = AttractionMapper.toDTO(attractionNode);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(
            @Valid @PathVariable Long id) {
        attractionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
