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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConexaoService {
    @Autowired
    private ConexaoRepository conexaoRepository;

    @Autowired
    private CityRepository cityRepository;

    public List<ConnectionResponseDTO> findAll() {
        List<Map<String, Object>> connections = conexaoRepository.findAllConnectionsWithCityInfo();
        List<ConnectionResponseDTO> response = new ArrayList<>();

        for (Map<String, Object> conn : connections) {
            response.add(mapToDTO(conn));
        }

        return response;
    }

    public ConnectionResponseDTO findById(Long id) {
        Map<String, Object> conn = conexaoRepository.findConnectionWithCityInfoById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Connection not found with ID: " + id));

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

            return new ConnectionResponseDTO(
                conexao.getId(),
                originCity.getId(),
                destinationCity.getId(),
                originCity.getName(),
                destinationCity.getName(),
                conexao.getDistancia(),
                conexao.getTempo(),
                conexao.getCreatedAt()
            );
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

    private ConnectionResponseDTO mapToDTO(Map<String, Object> data) {
        return new ConnectionResponseDTO(
            ((Number) data.get("connectionId")).longValue(),
            data.get("originCityId") != null ? ((Number) data.get("originCityId")).longValue() : null,
            data.get("destinationCityId") != null ? ((Number) data.get("destinationCityId")).longValue() : null,
            (String) data.get("originCityName"),
            (String) data.get("destinationCityName"),
            data.get("distance") != null ? ((Number) data.get("distance")).doubleValue() : null,
            data.get("time") != null ? ((Number) data.get("time")).doubleValue() : null,
            data.get("createdAt") != null ? (LocalDateTime) data.get("createdAt") : null
        );
    }
}
