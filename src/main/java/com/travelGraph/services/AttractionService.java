package com.travelGraph.services;

import com.travelGraph.dto.attraction.CreateAttractionRequestDTO;
import com.travelGraph.dto.attraction.UpdateAttractionRequestDTO;
import com.travelGraph.entities.AttractionNode;
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

@Service
public class AttractionService {
    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private CityRepository cityRepository;

    public List<AttractionNode> findAll() {
        return attractionRepository.findAll();
    }

    public AttractionNode findById(Long id) {
        Optional<AttractionNode> attraction = attractionRepository.findById(id);
        return attraction.orElseThrow(() -> new ResourceNotFoundException("Ponto turístico não encontrado"));
    }

    public List<AttractionNode> findByCityId(Long cityId) {
        return attractionRepository.findByCityId(cityId);
    }

    public AttractionNode create(CreateAttractionRequestDTO createAttractionDTO) {
        if (!cityRepository.existsById(createAttractionDTO.getCityId())) {
            throw new ResourceNotFoundException("Cidade não encontrada");
        }

        try {
            AttractionNode attractionNode = new AttractionNode();
            attractionNode.setName(createAttractionDTO.getName());
            attractionNode.setDescription(createAttractionDTO.getDescription());
            attractionNode.setCategory(createAttractionDTO.getCategory());
            attractionNode.setLatitude(createAttractionDTO.getLatitude());
            attractionNode.setLongitude(createAttractionDTO.getLongitude());
            attractionNode.setCreatedAt(LocalDateTime.now());

            AttractionNode savedAttraction = attractionRepository.save(attractionNode);
            attractionRepository.linkToCity(savedAttraction.getId(), createAttractionDTO.getCityId());
            return attractionRepository.findById(savedAttraction.getId()).orElse(savedAttraction);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Erro ao criar ponto turístico: " + e.getMessage());
        }
    }

    public AttractionNode update(Long id, UpdateAttractionRequestDTO updateAttractionDTO) {
        findById(id);

        try {
            attractionRepository.updateProperties(
                id,
                updateAttractionDTO.getName(),
                updateAttractionDTO.getDescription(),
                updateAttractionDTO.getCategory(),
                updateAttractionDTO.getLatitude(),
                updateAttractionDTO.getLongitude(),
                LocalDateTime.now()
            );
            return attractionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Ponto turístico não encontrado")
            );
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Erro ao atualizar ponto turístico: " + e.getMessage());
        }
    }

    public void delete(Long id) {
        try {
            findById(id);
            attractionRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Erro ao deletar ponto turístico: " + e.getMessage());
        }
    }
}
