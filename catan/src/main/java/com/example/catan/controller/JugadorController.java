package com.example.catan.controller;

import dto.JugadorRecursosDTO;
import com.example.catan.model.Construccio;
import com.example.catan.service.JugadorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jugador")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @GetMapping("/{idJugador}/recursos")
    public ResponseEntity<JugadorRecursosDTO> recursos(@PathVariable("idJugador") Integer idJugador) {
        return jugadorService.recursos(idJugador)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{idJugador}/construir")
    public String construir(@PathVariable("idJugador") Integer idJugador,
                            @RequestParam("tipus") Construccio tipus,
                            @RequestParam("posicio") Integer posicio) {
        return jugadorService.construir(idJugador, tipus, posicio);
    }

    @PostMapping("/{idJugador}/passar")
    public String passar(@PathVariable("idJugador") Integer idJugador) {
        return jugadorService.passarTorn(idJugador);
    }
}
