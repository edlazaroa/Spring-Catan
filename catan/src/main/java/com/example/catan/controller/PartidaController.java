package com.example.catan.controller;

import dto.*;
import com.example.catan.service.PartidaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partida")
public class PartidaController {

    private final PartidaService partidaService; 

    public PartidaController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    @GetMapping("/{idPartida}/tauler")
    public List<HexagonTaulerDTO> tauler(@PathVariable("idPartida") Integer idPartida) {
        return partidaService.tauler(idPartida);
    }

    @PostMapping
    public PartidaDTO iniciar(@RequestBody IniciarPartidaRequest request) {
        return partidaService.iniciar(request);
    }

    @PostMapping("/{idPartida}/daus")
    public TiradaDTO tirarDaus(@PathVariable("idPartida") Integer idPartida) {
        return partidaService.tirarDaus(idPartida);
    }
}
