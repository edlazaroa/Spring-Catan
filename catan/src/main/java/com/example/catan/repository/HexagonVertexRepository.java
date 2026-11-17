package com.example.catan.repository;

import com.example.catan.model.HexagonVertex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HexagonVertexRepository extends JpaRepository<HexagonVertex, Integer> {

    /** Tota la relacio hexagon-vertex d'una partida (per mostrar el tauler). */
    @Query("select hv from HexagonVertex hv join fetch hv.hexagon join fetch hv.vertex "
         + "where hv.hexagon.partida.id = :partida")
    List<HexagonVertex> findTaulerByPartida(@Param("partida") Integer partidaId);

    /** Hexagons que toquen un vertex (per als recursos inicials). */
    @Query("select hv from HexagonVertex hv join fetch hv.hexagon where hv.vertex.id = :vertex")
    List<HexagonVertex> findByVertex(@Param("vertex") Integer vertexId);
}
