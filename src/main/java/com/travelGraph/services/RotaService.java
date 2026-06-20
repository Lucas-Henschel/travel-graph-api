package com.travelGraph.services;

import com.travelGraph.dto.attraction.AttractionResponseDTO;
import com.travelGraph.dto.route.CityRouteDTO;
import com.travelGraph.dto.route.TravelRouteDTO;
import com.travelGraph.entities.AttractionNode;
import com.travelGraph.entities.CityConnection;
import com.travelGraph.entities.CityNode;
import com.travelGraph.mapper.AttractionMapper;
import com.travelGraph.repositories.AttractionRepository;
import com.travelGraph.repositories.CityRepository;
import com.travelGraph.repositories.ConexaoRepository;
import com.travelGraph.repositories.GraphQueryRepository;
import com.travelGraph.services.exceptions.DatabaseException;
import com.travelGraph.services.exceptions.InvalidRouteException;
import com.travelGraph.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RotaService {
    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private GraphQueryRepository graphQueryRepository;

    @Autowired
    private ConexaoRepository conexaoRepository;

    private static final String GRAPH_NAME = "travel_graph";

    /**
     * Calcula a rota mais curta entre duas cidades usando Neo4j GDS
     *
     * @param startCityId ID da cidade de origem
     * @param endCityId   ID da cidade de destino
     * @param criteria    "distance" ou "time" como peso
     * @return TravelRouteDTO with cities, total distance and total time
     */
    public TravelRouteDTO calcularRota(Long startCityId, Long endCityId, String criteria) {
        CityNode origem = cityRepository.findById(startCityId)
            .orElseThrow(() -> new ResourceNotFoundException("Cidade de origem não encontrada"));

        CityNode destino = cityRepository.findById(endCityId)
            .orElseThrow(() -> new ResourceNotFoundException("Cidade de destino não encontrada"));

        if (!criteria.equals("distance") && !criteria.equals("time")) {
            throw new InvalidRouteException("Critério inválido. Use 'distance' ou 'time'");
        }

        String propriedadePeso = criteria.equals("distance") ? "distancia" : "tempo";

        try {
            criarGrafo(propriedadePeso);

            List<Long> caminhoIds = executarDijkstra(startCityId, endCityId, propriedadePeso);

            if (caminhoIds.isEmpty()) {
                throw new InvalidRouteException(
                    "Nenhuma rota encontrada entre " + origem.getName() + " e " + destino.getName()
                );
            }

            TravelRouteDTO roteiro = construirRoteiro(caminhoIds, propriedadePeso);

            return roteiro;
        } finally {
            deletarGrafo();
        }
    }

    /**
     * Cria um grafo em memória com GDS
     */
    private void criarGrafo(String propriedadePeso) {
        try {
            graphQueryRepository.createGraph(
                GRAPH_NAME,
                propriedadePeso
            );
        } catch (Exception e) {
            throw new DatabaseException("Erro ao criar grafo em memória: " + e.getMessage());
        }
    }

    /**
     * Executa o algoritmo de Dijkstra
     */
    private List<Long> executarDijkstra(
        Long origemId,
        Long destinoId,
        String propriedadePeso
    ) {
        try {
            List<Long> nodeIds = graphQueryRepository.executeDijkstra(
                GRAPH_NAME,
                origemId,
                destinoId
            );

            if (nodeIds == null) {
                return new ArrayList<>();
            }

            return nodeIds;
        } catch (Exception e) {
            throw new DatabaseException("Erro ao executar o dijkstra: " + e.getMessage());
        }
    }

    /**
     * Deleta o grafo em memória
     */
    private void deletarGrafo() {
        try {
            graphQueryRepository.dropGraph(GRAPH_NAME);
        } catch (Exception e) {
            throw new DatabaseException("Erro ao deletar grafo (pode não existir): " + e.getMessage());
        }
    }

    /**
     * Constrói o DTO TravelRouteDTO com todas as informações
     */
    private TravelRouteDTO construirRoteiro(List<Long> caminhoIds, String propriedadePeso) {
        List<CityRouteDTO> cities = new ArrayList<>();
        Double totalDistance = 0.0;
        Double totalTime = 0.0;

        for (int i = 0; i < caminhoIds.size(); i++) {
            Long cityId = caminhoIds.get(i);
            CityNode city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("City not found: " + cityId));

            List<AttractionNode> attractions = attractionRepository.findByCityId(cityId);
            List<AttractionResponseDTO> attractionDTOs = attractions.stream()
                .map(AttractionMapper::toDTO)
                .collect(Collectors.toList());

            CityRouteDTO cityRouteDto = CityRouteDTO.fromCityNode(city, attractionDTOs);

            cities.add(cityRouteDto);

            if (i < caminhoIds.size() - 1) {
                double[] conexaoInfo = buscarConexaoInfo(cityId, caminhoIds.get(i + 1));
                totalDistance += conexaoInfo[0];
                totalTime += conexaoInfo[1];
            }
        }

        TravelRouteDTO travelRoute = new TravelRouteDTO();
        travelRoute.setCities(cities);
        travelRoute.setTotalDistance(totalDistance);
        travelRoute.setTotalTime(totalTime);

        return travelRoute;
    }

    /**
     * Busca distância e tempo entre duas cidades conectadas em uma única consulta
     */
    private double[] buscarConexaoInfo(Long origemId, Long destinoId) {
        try {
            Optional<CityConnection> conexao = conexaoRepository.findByOrigemAndDestino(origemId, destinoId);

            if (conexao.isPresent()) {
                Double distancia = conexao.get().getDistancia() != null ? conexao.get().getDistancia() : 0.0;
                Double tempo = conexao.get().getTempo() != null ? conexao.get().getTempo() : 0.0;
                return new double[]{distancia, tempo};
            }

            return new double[]{0.0, 0.0};
        } catch (Exception e) {
            throw new DatabaseException("Erro ao buscar informações da conexão: " + e.getMessage());
        }
    }
}
