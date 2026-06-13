package com.travelGraph.controller;

import com.travelGraph.dto.city.CreateCityRequestDTO;
import com.travelGraph.dto.city.CityResponseDTO;
import com.travelGraph.dto.city.UpdateCityRequestDTO;
import com.travelGraph.entities.CityNode;
import com.travelGraph.mapper.CityMapper;
import com.travelGraph.services.CityService;
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
@RequestMapping(value = "/cidades")
public class CityController {
    @Autowired
    private CityService cityService;

    @GetMapping
    public ResponseEntity<List<CityResponseDTO>> findAll() {
        log.info("GET /cidades - Finding all cities");
        List<CityNode> cityNodes = cityService.findAll();
        List<CityResponseDTO> response = new ArrayList<>();

        for (CityNode cityNode : cityNodes) {
            response.add(CityMapper.toDTO(cityNode));
        }

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CityResponseDTO> findById(@Valid @PathVariable Long id) {
        log.info("GET /cidades/{} - Finding city by id", id);
        CityNode cityNode = cityService.findById(id);
        CityResponseDTO response = CityMapper.toDTO(cityNode);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<CityResponseDTO> create(@Valid @RequestBody CreateCityRequestDTO createCityDTO) {
        log.info("POST /cidades - Creating new city: {}", createCityDTO.getName());
        CityNode cityNode = cityService.create(createCityDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(cityNode.getId()).toUri();

        CityResponseDTO response = CityMapper.toDTO(cityNode);

        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CityResponseDTO> update(@Valid @PathVariable Long id, @Valid @RequestBody UpdateCityRequestDTO updateCityDTO) {
        log.info("PUT /cidades/{} - Updating city", id);
        CityNode cityNode = cityService.update(id, updateCityDTO);
        CityResponseDTO response = CityMapper.toDTO(cityNode);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@Valid @PathVariable Long id) {
        log.info("DELETE /cidades/{} - Deleting city", id);
        cityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}




