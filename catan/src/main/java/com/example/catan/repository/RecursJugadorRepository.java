package com.example.catan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.catan.model.RecursJugador;
import com.example.catan.model.TipusRecurs;

public interface RecursJugadorRepository extends JpaRepository<RecursJugador, Integer> {

    @Query("select r from RecursJugador r where r.jugador.jugador_id = :id")
    List<RecursJugador> findByJugadorId(@Param("id") Integer id);

    @Query("select r from RecursJugador r where r.jugador.jugador_id = :id and r.tipus = :tipus")
    Optional<RecursJugador> findByJugadorIdAndTipus(@Param("id") Integer id, @Param("tipus") TipusRecurs tipus);
}
