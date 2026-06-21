package com.travelGraph.controller;

import com.travelGraph.dto.route.CalculateRouteRequestDTO;
import com.travelGraph.dto.route.TravelRouteDTO;
import com.travelGraph.services.RotaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/routes")
public class RotaController {
    @Autowired
    private RotaService rotaService;

    /**
     * Calcula a rota entre duas cidades com critério especificado
     */
    @PostMapping
    public ResponseEntity<TravelRouteDTO> calcularRota(@Valid @RequestBody CalculateRouteRequestDTO request) {
        TravelRouteDTO travelRoute = rotaService.calcularRota(request.getStartCityId(), request.getEndCityId(), request.getCriteria());
        return ResponseEntity.ok().body(travelRoute);
    }
}
