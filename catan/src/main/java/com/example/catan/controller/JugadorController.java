package com.example.catan.controller;

import com.example.catan.dto.Dtos.JugadorRecursosDTO;
import com.example.catan.model.Construccio;
import com.example.catan.service.JugadorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jugador")
public class JugadorController {

    private final JugadorService jugadorService; // Faltan los DTO y los Service

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    /** Jugador amb el seu inventari; 404 si no existeix. */
    @GetMapping("/{idJugador}/recursos")
    public ResponseEntity<JugadorRecursosDTO> recursos(@PathVariable("idJugador") Integer idJugador) {
        return jugadorService.recursos(idJugador)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Construeix CARRETERA, POBLAT o CIUTAT; posicio = id de l'aresta o del vertex. */
    @PostMapping("/{idJugador}/construir")
    public String construir(@PathVariable("idJugador") Integer idJugador,
                            @RequestParam("tipus") Construccio tipus,
                            @RequestParam("posicio") Integer posicio) {
        return jugadorService.construir(idJugador, tipus, posicio);
    }

    /** Passa el torn. */
    @PostMapping("/{idJugador}/passar")
    public String passar(@PathVariable("idJugador") Integer idJugador) {
        return jugadorService.passarTorn(idJugador);
    }
}
