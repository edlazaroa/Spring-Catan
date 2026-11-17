package com.example.catan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.catan.model.Jugador;

public interface JugadorRepository extends JpaRepository<Jugador, Integer>{
	List<Jugador> findByPartidaIdOrderByOrdreTorn(Integer partidaId);
    long countByPartidaId(Integer partidaId);
    Optional<Jugador> findByPartidaIdAndOrdreTorn(Integer partidaId, int ordreTorn);
}
