package com.example.catan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.catan.dto.JugadorRecursosDTO;
import com.example.catan.model.TipusConstruccio;
import com.example.catan.service.JugadorService;

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
                            @RequestParam("tipus") TipusConstruccio tipus,
                            @RequestParam("posicio") Integer posicio) {
        return jugadorService.construir(idJugador, tipus, posicio);
    }

    @PostMapping("/{idJugador}/passar")
    public String passar(@PathVariable("idJugador") Integer idJugador) {
        return jugadorService.passarTorn(idJugador);
    }
}
