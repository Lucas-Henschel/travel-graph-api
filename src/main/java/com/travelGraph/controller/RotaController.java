package com.travelGraph.controller;

import com.travelGraph.dto.route.RoteiroDTO;
import com.travelGraph.services.RotaService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/rotas")
public class RotaController {
    @Autowired
    private RotaService rotaService;

    @GetMapping
    public ResponseEntity<RoteiroDTO> calcularRota(
            @RequestParam @NotNull(message = "O ID da cidade de origem é obrigatório") Long origem,
            @RequestParam @NotNull(message = "O ID da cidade de destino é obrigatório") Long destino,
            @RequestParam(defaultValue = "distancia") String criterio) {
        
        log.info("GET /rotas - Calculando rota de {} para {} com critério: {}", origem, destino, criterio);
        
        RoteiroDTO roteiro = rotaService.calcularRota(origem, destino, criterio);
        return ResponseEntity.ok().body(roteiro);
    }
}

