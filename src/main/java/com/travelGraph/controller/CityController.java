package com.travelGraph.controller;

import com.travelGraph.dto.city.CreateCityRequestDTO;
import com.travelGraph.dto.city.CityResponseDTO;
import com.travelGraph.dto.city.UpdateCityRequestDTO;
import com.travelGraph.entities.CityNode;
import com.travelGraph.mapper.CityMapper;
import com.travelGraph.services.CityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/cities")
public class CityController {
    @Autowired
    private CityService cityService;

    /**
     * Lista todas as cidades
     */
    @GetMapping
    public ResponseEntity<List<CityResponseDTO>> findAll() {
        List<CityNode> cityNodes = cityService.findAll();
        List<CityResponseDTO> response = new ArrayList<>();

        for (CityNode cityNode : cityNodes) {
            response.add(CityMapper.toDTO(cityNode));
        }

        return ResponseEntity.ok().body(response);
    }

    /**
     * Busca uma cidade por ID
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<CityResponseDTO> findById(@Valid @PathVariable Long id) {
        CityNode cityNode = cityService.findById(id);
        CityResponseDTO response = CityMapper.toDTO(cityNode);

        return ResponseEntity.ok().body(response);
    }

    /**
     * Cria uma nova cidade
     */
    @PostMapping
    public ResponseEntity<CityResponseDTO> create(@Valid @RequestBody CreateCityRequestDTO createCityDTO) {
        CityNode cityNode = cityService.create(createCityDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(cityNode.getId()).toUri();

        CityResponseDTO response = CityMapper.toDTO(cityNode);

        return ResponseEntity.created(uri).body(response);
    }

    /**
     * Atualiza uma cidade existente
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<CityResponseDTO> update(@Valid @PathVariable Long id, @Valid @RequestBody UpdateCityRequestDTO updateCityDTO) {
        CityNode cityNode = cityService.update(id, updateCityDTO);
        CityResponseDTO response = CityMapper.toDTO(cityNode);

        return ResponseEntity.ok().body(response);
    }

    /**
     * Remove uma cidade
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@Valid @PathVariable Long id) {
        cityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
