package com.travelGraph.services;

import com.travelGraph.dto.attraction.CreateAttractionRequestDTO;
import com.travelGraph.dto.attraction.UpdateAttractionRequestDTO;
import com.travelGraph.entities.AttractionNode;
import com.travelGraph.entities.CityNode;
import com.travelGraph.repositories.AttractionRepository;
import com.travelGraph.repositories.CityRepository;
import com.travelGraph.services.exceptions.DatabaseException;
import com.travelGraph.services.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AttractionService {
    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private CityRepository cityRepository;

    public List<AttractionNode> findAll() {
        log.info("Finding all attractions");
        return attractionRepository.findAll();
    }

    public AttractionNode findById(Long id) {
        log.info("Finding attraction by id: {}", id);
        Optional<AttractionNode> attraction = attractionRepository.findById(id);
        return attraction.orElseThrow(() -> new ResourceNotFoundException("Ponto turístico não encontrado com ID: " + id));
    }

    public List<AttractionNode> findByCityId(Long cityId) {
        log.info("Finding attractions by city id: {}", cityId);
        return attractionRepository.findByCityId(cityId);
    }

    public AttractionNode create(CreateAttractionRequestDTO createAttractionDTO) {
        log.info("Creating new attraction: {}", createAttractionDTO.getName());

        CityNode city = cityRepository.findById(createAttractionDTO.getCityId())
            .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontrada com ID: " + createAttractionDTO.getCityId()));

        try {
            AttractionNode attractionNode = new AttractionNode();
            attractionNode.setName(createAttractionDTO.getName());
            attractionNode.setDescription(createAttractionDTO.getDescription());
            attractionNode.setCategory(createAttractionDTO.getCategory());
            attractionNode.setLatitude(createAttractionDTO.getLatitude());
            attractionNode.setLongitude(createAttractionDTO.getLongitude());
            attractionNode.setCity(city);
            attractionNode.setCreatedAt(LocalDateTime.now());

            AttractionNode savedAttraction = attractionRepository.save(attractionNode);
            log.info("Attraction created successfully with id: {}", savedAttraction.getId());
            return savedAttraction;
        } catch (DataIntegrityViolationException e) {
            log.error("Error creating attraction", e);
            throw new DatabaseException("Erro ao criar ponto turístico: " + e.getMessage());
        }
    }

    public AttractionNode update(Long id, UpdateAttractionRequestDTO updateAttractionDTO) {
        log.info("Updating attraction with id: {}", id);
        AttractionNode attractionNode = findById(id);

        try {
            attractionNode.setName(updateAttractionDTO.getName());
            attractionNode.setDescription(updateAttractionDTO.getDescription());
            attractionNode.setCategory(updateAttractionDTO.getCategory());
            attractionNode.setLatitude(updateAttractionDTO.getLatitude());
            attractionNode.setLongitude(updateAttractionDTO.getLongitude());
            attractionNode.setUpdatedAt(LocalDateTime.now());

            AttractionNode updatedAttraction = attractionRepository.save(attractionNode);
            log.info("Attraction updated successfully with id: {}", id);
            return updatedAttraction;
        } catch (DataIntegrityViolationException e) {
            log.error("Error updating attraction", e);
            throw new DatabaseException("Erro ao atualizar ponto turístico: " + e.getMessage());
        }
    }

    public void delete(Long id) {
        log.info("Deleting attraction with id: {}", id);
        try {
            findById(id);
            attractionRepository.deleteById(id);
            log.info("Attraction deleted successfully with id: {}", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Error deleting attraction", e);
            throw new DatabaseException("Erro ao deletar ponto turístico: " + e.getMessage());
        }
    }
}




