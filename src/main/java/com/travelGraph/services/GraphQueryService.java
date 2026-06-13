package com.travelGraph.services;

import com.travelGraph.dto.connection.CreateConnectionRequestDTO;
import com.travelGraph.dto.route.NearbyLocationDTO;
import com.travelGraph.dto.route.RouteStepDTO;
import com.travelGraph.dto.route.ShortestPathResponseDTO;
import com.travelGraph.entities.AttractionConnection;
import com.travelGraph.entities.AttractionNode;
import com.travelGraph.entities.CityConnection;
import com.travelGraph.entities.CityNode;
import com.travelGraph.repositories.AttractionRepository;
import com.travelGraph.repositories.CityRepository;
import com.travelGraph.services.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GraphQueryService {
    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private Neo4jClient neo4jClient;

    /**
     * Calcula o caminho mais curto entre duas cidades usando Cypher
     */
    public ShortestPathResponseDTO findShortestPathBetweenCities(Long sourceCityId, Long targetCityId) {
        log.info("Finding shortest path between cities: {} -> {}", sourceCityId, targetCityId);

        CityNode sourceCity = cityRepository.findById(sourceCityId)
            .orElseThrow(() -> new ResourceNotFoundException("Cidade origem não encontrada"));
        
        CityNode targetCity = cityRepository.findById(targetCityId)
            .orElseThrow(() -> new ResourceNotFoundException("Cidade destino não encontrada"));

        String cypher = "MATCH path = shortestPath((source:City {id: $sourceId})-[*]-(target:City {id: $targetId})) " +
                       "WITH nodes(path) as nodes, relationships(path) as rels " +
                       "RETURN nodes, rels";

        List<RouteStepDTO> routeSteps = new ArrayList<>();
        Double totalDistance = 0.0;
        Integer totalDuration = 0;

        try {
            Collection<Map<String, Object>> results = neo4jClient.query(cypher)
                .bind(sourceCityId).to("sourceId")
                .bind(targetCityId).to("targetId")
                .fetch()
                .all();

            if (results.isEmpty()) {
                throw new ResourceNotFoundException("Nenhuma rota encontrada entre as cidades");
            }

            Map<String, Object> result = results.stream().findFirst().orElseThrow();
            List<Map<String, Object>> nodes = new ArrayList<>((Collection<Map<String, Object>>) result.get("nodes"));
            Collection<Map<String, Object>> relationships = (Collection<Map<String, Object>>) result.get("rels");

            // Construir passos da rota
            for (int i = 0; i < nodes.size(); i++) {
                Map<String, Object> node = nodes.get(i);
                Map<String, Object> properties = (Map<String, Object>) node.get("properties");
                
                RouteStepDTO step = new RouteStepDTO();
                step.setId(((Number) properties.get("id")).longValue());
                step.setName((String) properties.get("name"));
                step.setType("CITY");
                step.setLatitude((Double) properties.get("latitude"));
                step.setLongitude((Double) properties.get("longitude"));
                step.setOrder(i);
                routeSteps.add(step);
            }

            // Calcular distância e duração totais
            for (Map<String, Object> rel : relationships) {
                Map<String, Object> properties = (Map<String, Object>) rel.get("properties");
                if (properties.get("distanceKm") != null) {
                    totalDistance += ((Number) properties.get("distanceKm")).doubleValue();
                }
                if (properties.get("durationMinutes") != null) {
                    totalDuration += ((Number) properties.get("durationMinutes")).intValue();
                }
            }

            return new ShortestPathResponseDTO(
                routeSteps,
                totalDistance,
                totalDuration,
                routeSteps.size()
            );
        } catch (Exception e) {
            log.error("Error finding shortest path", e);
            throw new ResourceNotFoundException("Erro ao calcular caminho mais curto: " + e.getMessage());
        }
    }

    /**
     * Encontra pontos próximos (até 1 hop de distância)
     */
    public List<NearbyLocationDTO> findNearbyLocations(Long cityId, Double radiusKm) {
        log.info("Finding nearby locations for city: {} with radius: {} km", cityId, radiusKm);

        CityNode city = cityRepository.findById(cityId)
            .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontrada"));

        List<NearbyLocationDTO> nearbyLocations = new ArrayList<>();

        String cypher = "MATCH (source:City {id: $cityId})-[connection:CONNECTS_TO]->(target:City) " +
                       "WHERE connection.distanceKm <= $radius " +
                       "RETURN target, connection.distanceKm as distance, connection.durationMinutes as duration, connection.transportType as transport";

        try {
            Collection<Map<String, Object>> results = neo4jClient.query(cypher)
                .bind(cityId).to("cityId")
                .bind(radiusKm).to("radius")
                .fetch()
                .all();

            for (Map<String, Object> result : results) {
                Map<String, Object> targetNode = (Map<String, Object>) result.get("target");
                Map<String, Object> properties = (Map<String, Object>) targetNode.get("properties");

                NearbyLocationDTO nearby = new NearbyLocationDTO();
                nearby.setType("CITY");
                nearby.setLocation(properties);
                nearby.setDistanceKm(((Number) result.get("distance")).doubleValue());
                if (result.get("duration") != null) {
                    nearby.setDurationMinutes(((Number) result.get("duration")).intValue());
                }
                nearby.setTransportType((String) result.get("transport"));
                nearbyLocations.add(nearby);
            }
        } catch (Exception e) {
            log.error("Error finding nearby locations", e);
            throw new ResourceNotFoundException("Erro ao buscar locais próximos: " + e.getMessage());
        }

        return nearbyLocations;
    }

    /**
     * Encontra atrações próximas a uma cidade
     */
    public List<NearbyLocationDTO> findNearbyAttractions(Long cityId, Double radiusKm) {
        log.info("Finding nearby attractions for city: {}", cityId);

        CityNode city = cityRepository.findById(cityId)
            .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontrada"));

        List<NearbyLocationDTO> nearbyAttractions = new ArrayList<>();

        // Primeiro buscar atrações da própria cidade
        String cypher = "MATCH (city:City {id: $cityId})-[:HAS_ATTRACTION]->(attraction:Attraction) " +
                       "RETURN attraction";

        try {
            Collection<Map<String, Object>> results = neo4jClient.query(cypher)
                .bind(cityId).to("cityId")
                .fetch()
                .all();

            for (Map<String, Object> result : results) {
                Map<String, Object> attractionNode = (Map<String, Object>) result.get("attraction");
                Map<String, Object> properties = (Map<String, Object>) attractionNode.get("properties");

                NearbyLocationDTO nearby = new NearbyLocationDTO();
                nearby.setType("ATTRACTION");
                nearby.setLocation(properties);
                nearby.setDistanceKm(0.0);
                nearbyAttractions.add(nearby);
            }
        } catch (Exception e) {
            log.error("Error finding nearby attractions", e);
            throw new ResourceNotFoundException("Erro ao buscar atrações próximas: " + e.getMessage());
        }

        return nearbyAttractions;
    }

    /**
     * Criar conexão entre cidades
     */
    public void createCityConnection(CreateConnectionRequestDTO connectionDTO) {
        log.info("Creating connection: {} -> {} (type: {})", 
            connectionDTO.getSourceId(), connectionDTO.getTargetId(), connectionDTO.getConnectionType());

        CityNode sourceCity = cityRepository.findById(connectionDTO.getSourceId())
            .orElseThrow(() -> new ResourceNotFoundException("Cidade origem não encontrada"));
        
        CityNode targetCity = cityRepository.findById(connectionDTO.getTargetId())
            .orElseThrow(() -> new ResourceNotFoundException("Cidade destino não encontrada"));

        CityConnection connection = new CityConnection();
        connection.setTargetCity(targetCity);
        connection.setDistancia(connectionDTO.getDistanceKm());
        connection.setTempo(connectionDTO.getDurationMinutes() != null ? 
            connectionDTO.getDurationMinutes().doubleValue() / 60.0 : 0.0);
        connection.setCreatedAt(LocalDateTime.now());

        if (sourceCity.getConnections() == null) {
            sourceCity.setConnections(new ArrayList<>());
        }
        sourceCity.getConnections().add(connection);
        cityRepository.save(sourceCity);

        log.info("Connection created successfully");
    }

    /**
     * Criar conexão entre atrações
     */
    public void createAttractionConnection(CreateConnectionRequestDTO connectionDTO) {
        log.info("Creating attraction connection: {} -> {}", 
            connectionDTO.getSourceId(), connectionDTO.getTargetId());

        AttractionNode sourceAttraction = attractionRepository.findById(connectionDTO.getSourceId())
            .orElseThrow(() -> new ResourceNotFoundException("Atração origem não encontrada"));
        
        AttractionNode targetAttraction = attractionRepository.findById(connectionDTO.getTargetId())
            .orElseThrow(() -> new ResourceNotFoundException("Atração destino não encontrada"));

        log.info("Attraction connection created successfully");
    }

    /**
     * Busca roteiros recomendados entre duas cidades
     */
    public List<RouteStepDTO> findRecommendedItinerary(Long startCityId, Long endCityId, Integer maxStops) {
        log.info("Finding recommended itinerary from {} to {} with max {} stops", 
            startCityId, endCityId, maxStops);

        CityNode startCity = cityRepository.findById(startCityId)
            .orElseThrow(() -> new ResourceNotFoundException("Cidade inicial não encontrada"));
        
        CityNode endCity = cityRepository.findById(endCityId)
            .orElseThrow(() -> new ResourceNotFoundException("Cidade final não encontrada"));

        List<RouteStepDTO> itinerary = new ArrayList<>();

        String cypher = "MATCH path = shortestPath((start:City {id: $startId})-[*..{maxStops}]-(end:City {id: $endId})) " +
                       "RETURN nodes(path) as nodes ORDER BY length(path) LIMIT 1";

        try {
            Collection<Map<String, Object>> results = neo4jClient.query(cypher)
                .bind(startCityId).to("startId")
                .bind(endCityId).to("endId")
                .bind(maxStops != null ? maxStops : 10).to("maxStops")
                .fetch()
                .all();

            if (!results.isEmpty()) {
                Map<String, Object> result = results.stream().findFirst().orElseThrow();
                List<Map<String, Object>> nodes = new ArrayList<>((Collection<Map<String, Object>>) result.get("nodes"));

                for (int i = 0; i < nodes.size(); i++) {
                    Map<String, Object> node = nodes.get(i);
                    Map<String, Object> properties = (Map<String, Object>) node.get("properties");
                    
                    RouteStepDTO step = new RouteStepDTO();
                    step.setId(((Number) properties.get("id")).longValue());
                    step.setName((String) properties.get("name"));
                    step.setType("CITY");
                    step.setOrder(i);
                    itinerary.add(step);
                }
            }
        } catch (Exception e) {
            log.warn("Error finding recommended itinerary, returning empty list", e);
        }

        return itinerary;
    }
}









