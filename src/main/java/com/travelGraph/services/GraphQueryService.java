package com.travelGraph.services;

import com.travelGraph.dto.connection.CreateConnectionRequestDTO;
import com.travelGraph.dto.route.NearbyLocationDTO;
import com.travelGraph.dto.route.RouteStepDTO;
import com.travelGraph.dto.route.ShortestPathResponseDTO;
import com.travelGraph.repositories.AttractionRepository;
import com.travelGraph.repositories.CityRepository;
import com.travelGraph.repositories.ConexaoRepository;
import com.travelGraph.repositories.GraphQueryRepository;
import com.travelGraph.services.exceptions.DatabaseException;
import com.travelGraph.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class GraphQueryService {
    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private GraphQueryRepository graphQueryRepository;

    @Autowired
    private ConexaoRepository conexaoRepository;

    /**
     * Calcula o caminho mais curto entre duas cidades
     */
    public ShortestPathResponseDTO findShortestPathBetweenCities(Long sourceCityId, Long targetCityId) {
        cityRepository.findById(sourceCityId)
            .orElseThrow(() -> new ResourceNotFoundException("Cidade origem não encontrada"));

        cityRepository.findById(targetCityId)
            .orElseThrow(() -> new ResourceNotFoundException("Cidade destino não encontrada"));

        List<RouteStepDTO> routeSteps = new ArrayList<>();
        Double totalDistance = 0.0;
        Integer totalDuration = 0;

        try {
            List<Map<String, Object>> results = graphQueryRepository.findShortestPath(sourceCityId, targetCityId);

            if (results.isEmpty()) {
                throw new ResourceNotFoundException("Nenhuma rota encontrada entre as cidades");
            }

            Map<String, Object> result = results.get(0);

            List<Map<String, Object>> nodes = new ArrayList<>((Collection<Map<String, Object>>) result.get("nodes"));

            Collection<Map<String, Object>> relationships = (Collection<Map<String, Object>>) result.get("rels");

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

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Erro ao calcular caminho mais curto: " + e.getMessage());
        }
    }

    /**
     * Encontra pontos próximos
     */
    public List<NearbyLocationDTO> findNearbyLocations(Long cityId, Double radiusKm) {
        cityRepository.findById(cityId)
            .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontrada"));

        List<NearbyLocationDTO> nearbyLocations = new ArrayList<>();

        try {
            List<Map<String, Object>> results = graphQueryRepository.findNearbyLocations(cityId, radiusKm);

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
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Erro ao buscar locais próximos: " + e.getMessage());
        }

        return nearbyLocations;
    }

    /**
     * Encontra atrações próximas
     */
    public List<NearbyLocationDTO> findNearbyAttractions(Long cityId, Double radiusKm) {
        cityRepository.findById(cityId)
            .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontrada"));

        Double radius = radiusKm != null ? radiusKm : 100.0;

        List<NearbyLocationDTO> nearbyAttractions = new ArrayList<>();

        try {
            List<Map<String, Object>> results = graphQueryRepository.findNearbyAttractions(cityId, radius);

            for (Map<String, Object> result : results) {
                Map<String, Object> attractionNode = (Map<String, Object>) result.get("attraction");
                Map<String, Object> properties = (Map<String, Object>) attractionNode.get("properties");

                NearbyLocationDTO nearby = new NearbyLocationDTO();
                nearby.setType("ATTRACTION");
                nearby.setLocation(properties);
                nearby.setDistanceKm(((Number) result.get("distance")).doubleValue());

                nearbyAttractions.add(nearby);
            }
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Erro ao buscar atrações próximas: " + e.getMessage());
        }

        return nearbyAttractions;
    }

    /**
     * Criar conexão entre cidades
     */
    public void createCityConnection(CreateConnectionRequestDTO connectionDTO) {
        if (!cityRepository.existsById(connectionDTO.getSourceId())) {
            throw new ResourceNotFoundException("Cidade origem não encontrada");
        }
        if (!cityRepository.existsById(connectionDTO.getTargetId())) {
            throw new ResourceNotFoundException("Cidade destino não encontrada");
        }

        Double tempo = connectionDTO.getDurationMinutes() != null
            ? connectionDTO.getDurationMinutes().doubleValue() / 60.0
            : 0.0;

        conexaoRepository.createConnection(
            connectionDTO.getSourceId(),
            connectionDTO.getTargetId(),
            connectionDTO.getDistanceKm(),
            tempo,
            LocalDateTime.now()
        );
    }

    /**
     * Criar conexão entre atrações
     */
    public void createAttractionConnection(CreateConnectionRequestDTO connectionDTO) {
        attractionRepository.findById(connectionDTO.getSourceId())
            .orElseThrow(() -> new ResourceNotFoundException("Atração origem não encontrada"));

        attractionRepository.findById(connectionDTO.getTargetId())
            .orElseThrow(() -> new ResourceNotFoundException("Atração destino não encontrada"));
    }

    /**
     * Busca roteiro recomendado
     */
    public List<RouteStepDTO> findRecommendedItinerary(Long startCityId, Long endCityId, Integer maxStops) {
        cityRepository.findById(startCityId)
            .orElseThrow(() -> new ResourceNotFoundException("Cidade inicial não encontrada"));

        cityRepository.findById(endCityId)
            .orElseThrow(() -> new ResourceNotFoundException("Cidade final não encontrada"));

        List<RouteStepDTO> itinerary = new ArrayList<>();

        try {
            List<Map<String, Object>> results = graphQueryRepository.findRecommendedItinerary(
                startCityId,
                endCityId
            );

            if (!results.isEmpty()) {
                Map<String, Object> result = results.get(0);

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

        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Erro ao buscar itinerário recomendado: " + e.getMessage());
        }

        return itinerary;
    }
}
