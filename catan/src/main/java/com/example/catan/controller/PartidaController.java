package com.example.catan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.catan.model.Partida;

import cat.iesabadell.videojocs.dto.LlancamentResposta;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
=======
import org.springframework.web.bind.annotation.*;

import com.example.catan.dto.NovaPartidaRequest;
import com.example.catan.service.PartidaService;
>>>>>>> a79cafb8d3ce4a28aac5ec5ab7f2ec485de9e0d4

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