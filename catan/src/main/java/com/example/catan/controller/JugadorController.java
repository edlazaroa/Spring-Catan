package com.example.catan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.catan.model.Jugador;

import cat.iesabadell.videojocs.dto.LlancamentResposta;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
=======
import org.springframework.web.bind.annotation.*;

import com.example.catan.service.JugadorService;
>>>>>>> a79cafb8d3ce4a28aac5ec5ab7f2ec485de9e0d4

@RestController
@RequestMapping("/jugador")
public class JugadorController {

    @Autowired
    private JugadorService service;

    // GET localhost:8080/jugador/1/recursos
    @GetMapping("/{idJugador}/recursos")
    public ResponseEntity<?> mostrarRecursos(@PathVariable int idJugador) {
        return service.mostrarRecursos(idJugador);
    }

    // POST localhost:8080/jugador/1/construir?tipus=POBLAT&posicio=12
    @PostMapping("/{idJugador}/construir")
    public ResponseEntity<String> construir(
            @PathVariable int idJugador,
            @RequestParam String tipus,
            @RequestParam int posicio) {
        return service.construir(idJugador, tipus, posicio);
    }

    // POST localhost:8080/jugador/1/passar
    @PostMapping("/{idJugador}/passar")
    public ResponseEntity<String> passarTorn(@PathVariable int idJugador) {
        return service.passarTorn(idJugador);
    }
}