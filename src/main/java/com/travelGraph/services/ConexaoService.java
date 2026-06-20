package com.travelGraph.services;

import com.travelGraph.dto.connection.ConnectionProjection;
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
import java.util.Optional;

@Service
public class ConexaoService {
    @Autowired
    private ConexaoRepository conexaoRepository;

    @Autowired
    private CityRepository cityRepository;

    public List<ConnectionResponseDTO> findAll() {
        return conexaoRepository.findAllConnectionsWithCityInfo()
            .stream()
            .map(this::toDTO)
            .toList();
    }

    public ConnectionResponseDTO findById(Long id) {
        ConnectionProjection projection = conexaoRepository.findConnectionWithCityInfoById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Conexão não encontrada"));

        return toDTO(projection);
    }

    private ConnectionResponseDTO toDTO(ConnectionProjection p) {
        return new ConnectionResponseDTO(
            p.connectionId(),
            p.originCityId(),
            p.destinationCityId(),
            p.originCityName(),
            p.destinationCityName(),
            p.distance(),
            p.time(),
            p.createdAt()
        );
    }

    public ConnectionResponseDTO create(ConnectionRequestDTO createConnectionDTO) {
        CityNode originCity = cityRepository.findById(createConnectionDTO.getOriginCityId())
            .orElseThrow(() -> new ResourceNotFoundException("Cidade de origem não encontrada"));

        CityNode destinationCity = cityRepository.findById(createConnectionDTO.getDestinationCityId())
            .orElseThrow(() -> new ResourceNotFoundException("Cidade de destino não encontrada"));

        Optional<CityConnection> existingConnection = conexaoRepository.findByOrigemAndDestino(
            createConnectionDTO.getOriginCityId(), createConnectionDTO.getDestinationCityId());

        if (existingConnection.isPresent()) {
            throw new ResourceAlreadyExistsException(
                "Já existe uma conexão entre " + originCity.getName() + " e " + destinationCity.getName()
            );
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
            throw new DatabaseException("Erro ao criar conexão: " + e.getMessage());
        }
    }

    public ConnectionResponseDTO update(Long id, ConnectionRequestDTO updateConnectionDTO) {
        ConnectionProjection existing = conexaoRepository.findConnectionWithCityInfoById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Conexão não encontrada"));

        CityNode originCity = cityRepository.findById(updateConnectionDTO.getOriginCityId())
            .orElseThrow(() -> new ResourceNotFoundException("Cidade de origem não encontrada"));

        CityNode destinationCity = cityRepository.findById(updateConnectionDTO.getDestinationCityId())
            .orElseThrow(() -> new ResourceNotFoundException("Cidade de destino não encontrada"));

        boolean endpointsChanged = !existing.originCityId().equals(updateConnectionDTO.getOriginCityId())
            || !existing.destinationCityId().equals(updateConnectionDTO.getDestinationCityId());

        if (endpointsChanged) {
            Optional<CityConnection> duplicate = conexaoRepository.findByOrigemAndDestino(
                updateConnectionDTO.getOriginCityId(), updateConnectionDTO.getDestinationCityId());

            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new ResourceAlreadyExistsException(
                    "Já existe uma conexão entre " + originCity.getName() + " e " + destinationCity.getName()
                );
            }
        }

        try {
            if (!endpointsChanged) {
                conexaoRepository.updateConnectionProperties(
                    id,
                    updateConnectionDTO.getDistance(),
                    updateConnectionDTO.getTime()
                );

                return new ConnectionResponseDTO(
                    id,
                    originCity.getId(),
                    destinationCity.getId(),
                    originCity.getName(),
                    destinationCity.getName(),
                    updateConnectionDTO.getDistance(),
                    updateConnectionDTO.getTime(),
                    existing.createdAt()
                );
            }

            conexaoRepository.deleteConnectionById(id);

            CityConnection conexao = new CityConnection();
            conexao.setTargetCity(destinationCity);
            conexao.setDistancia(updateConnectionDTO.getDistance());
            conexao.setTempo(updateConnectionDTO.getTime());
            conexao.setCreatedAt(existing.createdAt());

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
            throw new DatabaseException("Erro ao atualizar conexão: " + e.getMessage());
        }
    }

    public void delete(Long id) {
        try {
            findById(id);
            conexaoRepository.deleteConnectionById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Erro ao deletar conexão: " + e.getMessage());
        }
    }

}
