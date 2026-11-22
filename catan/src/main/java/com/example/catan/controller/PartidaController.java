package com.example.catan.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.catan.dto.HexagonTaulerDTO;
import com.example.catan.dto.IniciarPartidaRequest;
import com.example.catan.dto.PartidaDTO;
import com.example.catan.dto.TiradaDTO;
import com.example.catan.service.PartidaService;

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
