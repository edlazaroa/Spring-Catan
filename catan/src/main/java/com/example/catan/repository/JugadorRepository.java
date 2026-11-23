package com.example.catan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.catan.model.Jugador;

public interface JugadorRepository extends JpaRepository<Jugador, Integer> {

    @Query("select j from Jugador j where j.partida.partida_id = :id order by j.ordreTorn")
    List<Jugador> findByPartidaIdOrderByOrdreTorn(@Param("id") Integer id);

    @Query("select count(j) from Jugador j where j.partida.partida_id = :id")
    long countByPartidaId(@Param("id") Integer id);

    @Query("select j from Jugador j where j.partida.partida_id = :id and j.ordreTorn = :ordre")
    Optional<Jugador> findByPartidaIdAndOrdreTorn(@Param("id") Integer id, @Param("ordre") Integer ordre);
}
