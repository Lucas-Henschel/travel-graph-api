package com.travelGraph.services;

import com.travelGraph.dto.attraction.AttractionResponseDTO;
import com.travelGraph.dto.route.CidadeRotaDTO;
import com.travelGraph.dto.route.RoteiroDTO;
import com.travelGraph.entities.AttractionNode;
import com.travelGraph.entities.CityNode;
import com.travelGraph.mapper.AttractionMapper;
import com.travelGraph.mapper.CityMapper;
import com.travelGraph.repositories.CityRepository;
import com.travelGraph.repositories.AttractionRepository;
import com.travelGraph.services.exceptions.ResourceNotFoundException;
import com.travelGraph.services.exceptions.InvalidRouteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RotaService {
    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private Neo4jClient neo4jClient;

    private static final String GRAPH_NAME = "travel_graph";

    /**
     * Calcula a rota mais curta entre duas cidades usando Neo4j GDS
     * @param origemId ID da cidade de origem
     * @param destinoId ID da cidade de destino
     * @param criterio "distancia" ou "tempo" como peso
     * @return RoteiroDTO com as cidades, distância total e tempo total
     */
    public RoteiroDTO calcularRota(Long origemId, Long destinoId, String criterio) {
        log.info("Calculando rota de {} para {} com critério: {}", origemId, destinoId, criterio);

        // Validar cidades
        CityNode origem = cityRepository.findById(origemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cidade de origem não encontrada: " + origemId));
        
        CityNode destino = cityRepository.findById(destinoId)
            .orElseThrow(() -> new ResourceNotFoundException("Cidade de destino não encontrada: " + destinoId));

        if (!criterio.equals("distancia") && !criterio.equals("tempo")) {
            throw new InvalidRouteException("Critério inválido. Use 'distancia' ou 'tempo'");
        }

        String propriedadePeso = criterio.equals("distancia") ? "distancia" : "tempo";

        try {
            // 1. Criar grafo em memória
            criarGrafo(propriedadePeso);

            // 2. Executar algoritmo de caminho mais curto
            List<Long> caminhoIds = executarDijkstra(origemId, destinoId, propriedadePeso);

            if (caminhoIds.isEmpty()) {
                throw new InvalidRouteException("Nenhuma rota encontrada entre as cidades informadas");
            }

            // 3. Construir resposta
            RoteiroDTO roteiro = construirRoteiro(caminhoIds, propriedadePeso);

            log.info("Rota calculada com sucesso: {} cidades", roteiro.getCidades().size());
            return roteiro;

        } finally {
            // 4. Limpar grafo em memória
            deletarGrafo();
        }
    }

    /**
     * Cria um grafo em memória com GDS
     */
    private void criarGrafo(String propriedadePeso) {
        log.info("Criando grafo em memória para critério: {}", propriedadePeso);

        String cypher = "CALL gds.graph.project(\n" +
            "    $graphName,\n" +
            "    'Cidade',\n" +
            "    'CONECTA',\n" +
            "    { relationshipProperties: { weight: { property: $weight, defaultValue: 1.0 } } }\n" +
            ")\n" +
            "YIELD graphName, nodeCount, relationshipCount\n" +
            "RETURN graphName, nodeCount, relationshipCount";

        try {
            neo4jClient.query(cypher)
                .bind(GRAPH_NAME).to("graphName")
                .bind(propriedadePeso).to("weight")
                .fetch()
                .one();
            
            log.info("Grafo criado com sucesso");
        } catch (Exception e) {
            log.error("Erro ao criar grafo", e);
            throw new RuntimeException("Erro ao criar grafo em memória: " + e.getMessage());
        }
    }

    /**
     * Executa o algoritmo de Dijkstra
     */
    private List<Long> executarDijkstra(Long origemId, Long destinoId, String propriedadePeso) {
        log.info("Executando Dijkstra de {} para {}", origemId, destinoId);

        String cypher = "CALL gds.shortestPath.dijkstra.stream(\n" +
            "    $graphName,\n" +
            "    { sourceNode: $origem, targetNode: $destino, relationshipWeightProperty: 'weight' }\n" +
            ")\n" +
            "YIELD nodeIds, costs\n" +
            "RETURN nodeIds, costs";

        try {
            var result = neo4jClient.query(cypher)
                .bind(GRAPH_NAME).to("graphName")
                .bind(origemId).to("origem")
                .bind(destinoId).to("destino")
                .fetch()
                .one();

            if (result.isEmpty()) {
                return new ArrayList<>();
            }

            Map<String, Object> data = result.get();
            List<Long> nodeIds = (List<Long>) data.get("nodeIds");
            
            log.info("Caminho encontrado com {} cidades", nodeIds.size());
            return nodeIds;

        } catch (Exception e) {
            log.warn("Erro ao executar Dijkstra ou nenhuma rota encontrada", e);
            return new ArrayList<>();
        }
    }

    /**
     * Deleta o grafo em memória
     */
    private void deletarGrafo() {
        log.info("Deletando grafo em memória");

        String cypher = "CALL gds.graph.drop($graphName) YIELD graphName\n" +
            "RETURN graphName";

        try {
            neo4jClient.query(cypher)
                .bind(GRAPH_NAME).to("graphName")
                .fetch()
                .one();
            
            log.info("Grafo deletado com sucesso");
        } catch (Exception e) {
            log.warn("Erro ao deletar grafo (pode não existir)", e);
        }
    }

    /**
     * Constrói o DTO RoteiroDTO com todas as informações
     */
    private RoteiroDTO construirRoteiro(List<Long> caminhoIds, String propriedadePeso) {
        log.info("Construindo roteiro com {} cidades", caminhoIds.size());

        List<CidadeRotaDTO> cidades = new ArrayList<>();
        Double distanciaTotal = 0.0;
        Double tempoTotal = 0.0;

        // Processar cada cidade no caminho
        for (int i = 0; i < caminhoIds.size(); i++) {
            Long cidadeId = caminhoIds.get(i);
            CityNode city = cityRepository.findById(cidadeId)
                .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontrada: " + cidadeId));

            // Buscar pontos turísticos da cidade
            List<AttractionNode> attractions = attractionRepository.findByCityId(cidadeId);
            List<AttractionResponseDTO> attractionDTOs = attractions.stream()
                .map(AttractionMapper::toDTO)
                .collect(Collectors.toList());

            // Criar DTO da cidade
            CidadeRotaDTO cidadeDto = new CidadeRotaDTO();
            cidadeDto.setId(city.getId());
            cidadeDto.setNome(city.getName());
            cidadeDto.setLatitude(city.getLatitude());
            cidadeDto.setLongitude(city.getLongitude());
            cidadeDto.setPontosTuristicos(attractionDTOs);

            cidades.add(cidadeDto);

            // Calcular distância e tempo até a próxima cidade
            if (i < caminhoIds.size() - 1) {
                Double distancia = buscarDistanciaConexao(cidadeId, caminhoIds.get(i + 1));
                Double tempo = buscarTempoConexao(cidadeId, caminhoIds.get(i + 1));

                if (distancia != null) distanciaTotal += distancia;
                if (tempo != null) tempoTotal += tempo;
            }
        }

        RoteiroDTO roteiro = new RoteiroDTO();
        roteiro.setCidades(cidades);
        roteiro.setDistanciaTotal(distanciaTotal);
        roteiro.setTempoTotal(tempoTotal);

        return roteiro;
    }

    /**
     * Busca a distância entre duas cidades conectadas
     */
    private Double buscarDistanciaConexao(Long origemId, Long destinoId) {
        String cypher = "MATCH (origem:Cidade {id: $origemId})-[conn:CONECTA]->(destino:Cidade {id: $destinoId}) " +
            "RETURN conn.distancia as distancia";

        try {
            var result = neo4jClient.query(cypher)
                .bind(origemId).to("origemId")
                .bind(destinoId).to("destinoId")
                .fetch()
                .one();

            if (result.isPresent()) {
                return ((Number) result.get().get("distancia")).doubleValue();
            }
        } catch (Exception e) {
            log.warn("Erro ao buscar distância entre cidades", e);
        }
        return 0.0;
    }

    /**
     * Busca o tempo entre duas cidades conectadas
     */
    private Double buscarTempoConexao(Long origemId, Long destinoId) {
        String cypher = "MATCH (origem:Cidade {id: $origemId})-[conn:CONECTA]->(destino:Cidade {id: $destinoId}) " +
            "RETURN conn.tempo as tempo";

        try {
            var result = neo4jClient.query(cypher)
                .bind(origemId).to("origemId")
                .bind(destinoId).to("destinoId")
                .fetch()
                .one();

            if (result.isPresent()) {
                return ((Number) result.get().get("tempo")).doubleValue();
            }
        } catch (Exception e) {
            log.warn("Erro ao buscar tempo entre cidades", e);
        }
        return 0.0;
    }
}


