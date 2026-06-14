package com.travelGraph.services;

import com.travelGraph.dto.city.CreateCityRequestDTO;
import com.travelGraph.dto.city.UpdateCityRequestDTO;
import com.travelGraph.entities.CityNode;
import com.travelGraph.repositories.CityRepository;
import com.travelGraph.services.exceptions.DatabaseException;
import com.travelGraph.services.exceptions.ResourceAlreadyExistsException;
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
public class CityService {
    @Autowired
    private CityRepository cityRepository;

    public List<CityNode> findAll() {
        return cityRepository.findAll();
    }

    public CityNode findById(Long id) {
        Optional<CityNode> city = cityRepository.findById(id);
        return city.orElseThrow(() -> new ResourceNotFoundException("Cidade não encontrada com ID: " + id));
    }

    public CityNode create(CreateCityRequestDTO createCityDTO) {
        Optional<CityNode> existingCity = cityRepository.findByName(createCityDTO.getName());
        if (existingCity.isPresent()) {
            throw new ResourceAlreadyExistsException("Já existe uma cidade com esse nome: " + createCityDTO.getName());
        }

        try {
            CityNode cityNode = new CityNode();
            cityNode.setName(createCityDTO.getName());
            cityNode.setLatitude(createCityDTO.getLatitude());
            cityNode.setLongitude(createCityDTO.getLongitude());
            cityNode.setCreatedAt(LocalDateTime.now());

            CityNode savedCity = cityRepository.save(cityNode);
            return savedCity;
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Erro ao criar cidade: " + e.getMessage());
        }
    }

    public CityNode update(Long id, UpdateCityRequestDTO updateCityDTO) {
        CityNode cityNode = findById(id);

        Optional<CityNode> existingCity = cityRepository.findByName(updateCityDTO.getName());
        if (existingCity.isPresent() && !existingCity.get().getId().equals(id)) {
            throw new ResourceAlreadyExistsException("Já existe uma cidade com esse nome: " + updateCityDTO.getName());
        }

        try {
            cityNode.setName(updateCityDTO.getName());
            cityNode.setLatitude(updateCityDTO.getLatitude());
            cityNode.setLongitude(updateCityDTO.getLongitude());
            cityNode.setUpdatedAt(LocalDateTime.now());

            CityNode updatedCity = cityRepository.save(cityNode);
            return updatedCity;
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Erro ao atualizar cidade: " + e.getMessage());
        }
    }

    public void delete(Long id) {
        try {
            findById(id);
            cityRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Erro ao deletar cidade: " + e.getMessage());
        }
    }
}




