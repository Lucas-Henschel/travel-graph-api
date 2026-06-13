package com.travelGraph.controller;

import com.travelGraph.dto.connection.CreateConnectionRequestDTO;
import com.travelGraph.dto.route.NearbyLocationDTO;
import com.travelGraph.dto.route.RouteStepDTO;
import com.travelGraph.dto.route.ShortestPathResponseDTO;
import com.travelGraph.services.GraphQueryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/graph")
public class GraphController {
    @Autowired
    private GraphQueryService graphQueryService;

    /**
     * Calcula o caminho mais curto entre duas cidades
     * GET /graph/shortest-path?source=1&target=2
     */
    @GetMapping(value = "/shortest-path")
    public ResponseEntity<ShortestPathResponseDTO> getShortestPath(
            @RequestParam(name = "source") Long sourceId,
            @RequestParam(name = "target") Long targetId) {
        log.info("GET /graph/shortest-path - Finding shortest path from {} to {}", sourceId, targetId);
        ShortestPathResponseDTO result = graphQueryService.findShortestPathBetweenCities(sourceId, targetId);
        return ResponseEntity.ok().body(result);
    }

    /**
     * Encontra cidades próximas com raio específico
     * GET /graph/nearby-cities?cityId=1&radius=100
     */
    @GetMapping(value = "/nearby-cities")
    public ResponseEntity<List<NearbyLocationDTO>> getNearby(
            @RequestParam(name = "cityId") Long cityId,
            @RequestParam(name = "radius", defaultValue = "100") Double radius) {
        log.info("GET /graph/nearby-cities - Finding nearby cities for city {} with radius {}", cityId, radius);
        List<NearbyLocationDTO> result = graphQueryService.findNearbyLocations(cityId, radius);
        return ResponseEntity.ok().body(result);
    }

    /**
     * Encontra atrações próximas a uma cidade
     * GET /graph/nearby-attractions?cityId=1
     */
    @GetMapping(value = "/nearby-attractions")
    public ResponseEntity<List<NearbyLocationDTO>> getNearbyAttractions(
            @RequestParam(name = "cityId") Long cityId) {
        log.info("GET /graph/nearby-attractions - Finding nearby attractions for city {}", cityId);
        List<NearbyLocationDTO> result = graphQueryService.findNearbyAttractions(cityId, null);
        return ResponseEntity.ok().body(result);
    }

    /**
     * Encontra um roteiro recomendado entre duas cidades
     * GET /graph/itinerary?start=1&end=2&maxStops=5
     */
    @GetMapping(value = "/itinerary")
    public ResponseEntity<List<RouteStepDTO>> getRecommendedItinerary(
            @RequestParam(name = "start") Long startCityId,
            @RequestParam(name = "end") Long endCityId,
            @RequestParam(name = "maxStops", required = false) Integer maxStops) {
        log.info("GET /graph/itinerary - Finding recommended itinerary from {} to {}", startCityId, endCityId);
        List<RouteStepDTO> result = graphQueryService.findRecommendedItinerary(startCityId, endCityId, maxStops);
        return ResponseEntity.ok().body(result);
    }

    /**
     * Cria uma conexão entre cidades ou atrações
     * POST /graph/connections
     */
    @PostMapping(value = "/connections")
    public ResponseEntity<Void> createConnection(@Valid @RequestBody CreateConnectionRequestDTO connectionDTO) {
        log.info("POST /graph/connections - Creating connection from {} to {} (type: {})", 
            connectionDTO.getSourceId(), connectionDTO.getTargetId(), connectionDTO.getConnectionType());
        
        if ("CITY".equalsIgnoreCase(connectionDTO.getConnectionType())) {
            graphQueryService.createCityConnection(connectionDTO);
        } else if ("ATTRACTION".equalsIgnoreCase(connectionDTO.getConnectionType())) {
            graphQueryService.createAttractionConnection(connectionDTO);
        }

        return ResponseEntity.noContent().build();
    }
}

