package com.travelGraph.controller;

import com.travelGraph.dto.connection.CreateConnectionRequestDTO;
import com.travelGraph.dto.route.NearbyLocationDTO;
import com.travelGraph.dto.route.RouteStepDTO;
import com.travelGraph.dto.route.ShortestPathResponseDTO;
import com.travelGraph.services.GraphQueryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/graph")
public class GraphController {
    @Autowired
    private GraphQueryService graphQueryService;

    /**
     * Calcula o caminho mais curto entre duas cidades
     * GET /graph/shortestPath?source=1&target=2
     */
    @GetMapping(value = "/shortestPath")
    public ResponseEntity<ShortestPathResponseDTO> getShortestPath(
            @RequestParam(name = "source") Long sourceId,
            @RequestParam(name = "target") Long targetId) {
        ShortestPathResponseDTO result = graphQueryService.findShortestPathBetweenCities(sourceId, targetId);
        return ResponseEntity.ok().body(result);
    }

    /**
     * Encontra cidades próximas com raio específico
     * GET /graph/nearbyCities?cityId=1&radius=100
     */
    @GetMapping(value = "/nearbyCities")
    public ResponseEntity<List<NearbyLocationDTO>> getNearby(
            @RequestParam(name = "cityId") Long cityId,
            @RequestParam(name = "radius", defaultValue = "100") Double radius) {
        List<NearbyLocationDTO> result = graphQueryService.findNearbyLocations(cityId, radius);
        return ResponseEntity.ok().body(result);
    }

    /**
     * Encontra atrações próximas a uma cidade
     * GET /graph/nearbyAttractions?cityId=1
     */
    @GetMapping(value = "/nearbyAttractions")
    public ResponseEntity<List<NearbyLocationDTO>> getNearbyAttractions(
            @RequestParam(name = "cityId") Long cityId) {
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
        List<RouteStepDTO> result = graphQueryService.findRecommendedItinerary(startCityId, endCityId, maxStops);
        return ResponseEntity.ok().body(result);
    }

    /**
     * Cria uma conexão entre cidades ou atrações
     * POST /graph/connections
     */
    @PostMapping(value = "/connections")
    public ResponseEntity<Void> createConnection(
            @Valid @RequestBody CreateConnectionRequestDTO connectionDTO) {

        if ("CITY".equalsIgnoreCase(connectionDTO.getConnectionType())) {
            graphQueryService.createCityConnection(connectionDTO);
        } else if ("ATTRACTION".equalsIgnoreCase(connectionDTO.getConnectionType())) {
            graphQueryService.createAttractionConnection(connectionDTO);
        }

        return ResponseEntity.noContent().build();
    }
}

