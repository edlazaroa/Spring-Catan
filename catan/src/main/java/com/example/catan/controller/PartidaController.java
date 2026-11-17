package com.example.catan.controller;

import com.example.catan.dto.Dtos.*;
import com.example.catan.service.PartidaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partida")
public class PartidaController {

    private final PartidaService partidaService; // Faltan los .service

    public PartidaController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    /** Hexagons de la partida amb terreny, numero, lladre i construccions adjacents. */
    @GetMapping("/{idPartida}/tauler")
    public List<HexagonTaulerDTO> tauler(@PathVariable("idPartida") Integer idPartida) {
        return partidaService.tauler(idPartida);
    }

    /** Inicialitza una partida: {"jugadors":["Anna","Pau"], "puntsPerGuanyar":10}. */
    @PostMapping
    public PartidaDTO iniciar(@RequestBody IniciarPartidaRequest request) { // Faltan los DTO
        return partidaService.iniciar(request);
    }

    /** Tira els daus i executa la produccio. */
    @PostMapping("/{idPartida}/daus")
    public TiradaDTO tirarDaus(@PathVariable("idPartida") Integer idPartida) {
        return partidaService.tirarDaus(idPartida);
    }
}
