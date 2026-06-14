package com.travelGraph.services;

import com.travelGraph.dto.connection.ConnectionRequestDTO;
import com.travelGraph.dto.connection.ConnectionResponseDTO;
import com.travelGraph.entities.CityConnection;
import com.travelGraph.entities.CityNode;
import com.travelGraph.repositories.CityRepository;
import com.travelGraph.repositories.ConexaoRepository;
import com.travelGraph.services.exceptions.DatabaseException;
import com.travelGraph.services.exceptions.ResourceAlreadyExistsException;
import com.travelGraph.services.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConexaoService {
    @Autowired
    private ConexaoRepository conexaoRepository;

    @Autowired
    private CityRepository cityRepository;

    public List<ConnectionResponseDTO> findAll() {
        List<CityConnection> connections = conexaoRepository.findAllWithCities();
        List<ConnectionResponseDTO> response = new ArrayList<>();

        for (CityConnection conn : connections) {
            response.add(mapToDTO(conn));
        }

        return response;
    }

    public ConnectionResponseDTO findById(Long id) {
        Optional<CityConnection> conexao = conexaoRepository.findById(id);

        CityConnection conn = conexao.orElseThrow(() -> new ResourceNotFoundException("Connection not found with ID: " + id));

        return mapToDTO(conn);
    }

    public ConnectionResponseDTO create(ConnectionRequestDTO createConnectionDTO) {
        CityNode originCity = cityRepository.findById(createConnectionDTO.getOriginCityId())
            .orElseThrow(() -> new ResourceNotFoundException("Origin city not found with ID: " + createConnectionDTO.getOriginCityId()));

        CityNode destinationCity = cityRepository.findById(createConnectionDTO.getDestinationCityId())
            .orElseThrow(() -> new ResourceNotFoundException("Destination city not found with ID: " + createConnectionDTO.getDestinationCityId()));

        Optional<CityConnection> existingConnection = conexaoRepository.findByOrigemAndDestino(
            createConnectionDTO.getOriginCityId(), createConnectionDTO.getDestinationCityId());

        if (existingConnection.isPresent()) {
            throw new ResourceAlreadyExistsException("A connection between these cities already exists");
        }

        try {
            CityConnection conexao = new CityConnection();
            conexao.setTargetCity(destinationCity);
            conexao.setDistancia(createConnectionDTO.getDistance());
            conexao.setTempo(createConnectionDTO.getTime());
            conexao.setCreatedAt(LocalDateTime.now());

            if (originCity.getConnections() == null) {
                originCity.setConnections(new ArrayList<>());
            }

            originCity.getConnections().add(conexao);

            cityRepository.save(originCity);

            return mapToDTO(conexao);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Error creating connection: " + e.getMessage());
        }
    }

    public void delete(Long id) {
        try {
            findById(id);
            conexaoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Error deleting connection: " + e.getMessage());
        }
    }

    private ConnectionResponseDTO mapToDTO(CityConnection connection) {
        return new ConnectionResponseDTO(
            connection.getId(),
            connection.getTargetCity() != null ? connection.getTargetCity().getId() : null,
            connection.getTargetCity() != null ? connection.getTargetCity().getId() : null,
            connection.getTargetCity() != null ? connection.getTargetCity().getName() : null,
            connection.getTargetCity() != null ? connection.getTargetCity().getName() : null,
            connection.getDistancia(),
            connection.getTempo(),
            connection.getCreatedAt()
        );
    }
}
