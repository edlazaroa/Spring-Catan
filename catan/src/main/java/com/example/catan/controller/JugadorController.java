package com.example.catan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.catan.service.JugadorService;

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