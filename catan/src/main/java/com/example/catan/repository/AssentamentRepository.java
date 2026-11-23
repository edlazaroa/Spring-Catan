package com.example.catan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.catan.model.Assentament;

public interface AssentamentRepository extends JpaRepository<Assentament, Integer> {

    @Query("select a from Assentament a where a.vertex.partida.partida_id = :id")
    List<Assentament> findByPartida(@Param("id") Integer id);

    @Query("select count(a) > 0 from Assentament a where a.vertex.vertex_id = :v")
    boolean existsByVertexId(@Param("v") Integer v);

    /** Regla de la distancia: assentaments als vertexs veins (units per una aresta). */
    @Query("select count(a) from Assentament a where "
         + "a.vertex in (select ar.vertexB from Aresta ar where ar.vertexA.vertex_id = :v) or "
         + "a.vertex in (select ar.vertexA from Aresta ar where ar.vertexB.vertex_id = :v)")
    long countAdjacents(@Param("v") Integer v);

    @Query("select count(a) from Assentament a where a.jugador.jugador_id = :j "
         + "and a.vertex.vertex_id in (:va, :vb)")
    long countPropisEnVertexs(@Param("j") Integer j, @Param("va") Integer va, @Param("vb") Integer vb);
}
