package com.example.catan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.catan.model.Ciutat;

public interface CiutatRepository extends JpaRepository<Ciutat, Integer> {

    @Query("select count(c) from Ciutat c where c.jugador.jugador_id = :j")
    long countByPropietariId(@Param("j") Integer j);

    @Query("select c.jugador, hx.terreny from HexagonVertex hv join hv.hexagon hx "
         + "join Ciutat c on c.vertex = hv.vertex "
         + "where hx.partida.partida_id = :partida and hx.numero = :tirada "
         + "and hx.hexagon_id <> :lladre")
    List<Object[]> produccio(@Param("partida") Integer partida, @Param("tirada") Integer tirada,
                             @Param("lladre") Integer lladre);
}
