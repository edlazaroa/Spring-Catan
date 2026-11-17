package com.example.catan.repository;

import com.example.catan.model.RecursJugador;
import com.example.catan.model.TipusRecurs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecursJugadorRepository extends JpaRepository<RecursJugador, Integer> {
    Optional<RecursJugador> findByJugadorIdAndTipus(Integer jugadorId, TipusRecurs tipus);
    List<RecursJugador> findByJugadorId(Integer jugadorId);
}
