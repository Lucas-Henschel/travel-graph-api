package com.travelGraph.services;

import com.travelGraph.dto.connection.ConexaoRequestDTO;
import com.travelGraph.dto.connection.ConexaoResponseDTO;
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

    public List<ConexaoResponseDTO> findAll() {
        List<CityConnection> connections = conexaoRepository.findAllWithCities();
        List<ConexaoResponseDTO> response = new ArrayList<>();

        for (CityConnection conn : connections) {
            response.add(mapToDTO(conn));
        }

        return response;
    }

    public ConexaoResponseDTO findById(Long id) {
        Optional<CityConnection> conexao = conexaoRepository.findById(id);

        CityConnection conn = conexao.orElseThrow(() -> new ResourceNotFoundException("Conexão não encontrada com ID: " + id));

        return mapToDTO(conn);
    }

    public ConexaoResponseDTO create(ConexaoRequestDTO createConexaoDTO) {
        CityNode cidadeOrigem = cityRepository.findById(createConexaoDTO.getCidadeOrigemId())
            .orElseThrow(() -> new ResourceNotFoundException("Cidade de origem não encontrada com ID: " + createConexaoDTO.getCidadeOrigemId()));

        CityNode cidadeDestino = cityRepository.findById(createConexaoDTO.getCidadeDestinoId())
            .orElseThrow(() -> new ResourceNotFoundException("Cidade de destino não encontrada com ID: " + createConexaoDTO.getCidadeDestinoId()));

        Optional<CityConnection> existingConnection = conexaoRepository.findByOrigemAndDestino(
            createConexaoDTO.getCidadeOrigemId(), createConexaoDTO.getCidadeDestinoId());

        if (existingConnection.isPresent()) {
            throw new ResourceAlreadyExistsException("Já existe uma conexão entre essas cidades");
        }

        try {
            CityConnection conexao = new CityConnection();
            conexao.setTargetCity(cidadeDestino);
            conexao.setDistancia(createConexaoDTO.getDistancia());
            conexao.setTempo(createConexaoDTO.getTempo());
            conexao.setCreatedAt(LocalDateTime.now());

            if (cidadeOrigem.getConnections() == null) {
                cidadeOrigem.setConnections(new ArrayList<>());
            }

            cidadeOrigem.getConnections().add(conexao);

            cityRepository.save(cidadeOrigem);

            return mapToDTO(conexao);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Erro ao criar conexão: " + e.getMessage());
        }
    }

    public void delete(Long id) {
        try {
            findById(id);
            conexaoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Erro ao deletar conexão: " + e.getMessage());
        }
    }

    private ConexaoResponseDTO mapToDTO(CityConnection connection) {
        return new ConexaoResponseDTO(
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
