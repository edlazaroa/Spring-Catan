package com.example.catan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.catan.dto.NovaPartidaRequest;
import com.example.catan.service.PartidaService;

@RestController
@RequestMapping("/partida")
public class PartidaController {

    @Autowired
    private PartidaService service;

    // 1. GET localhost:8080/partida/1/tauler
    // Mostra els hexagons de la partida amb terreny, número, lladre i construccions adjacents
    @GetMapping("/{idPartida}/tauler")
    public ResponseEntity<?> mostrarTauler(@PathVariable int idPartida) {
        return service.mostrarTauler(idPartida);
    }

    // 2. POST localhost:8080/partida
    // Inicialitza una nova partida rebent un JSON amb els noms dels jugadors i (opcional) puntsPerGuanyar
    @PostMapping
    public ResponseEntity<?> iniciarPartida(@RequestBody NovaPartidaRequest request) {
        return service.iniciarPartida(request);
    }

    // 3. POST localhost:8080/partida/1/daus
    // Tira els daus i executa la producció de recursos
    @PostMapping("/{idPartida}/daus")
    public ResponseEntity<?> tirarDaus(@PathVariable int idPartida) {
        return service.tirarDaus(idPartida);
    }
}