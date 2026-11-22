package com.example.catan.repository;

import com.example.catan.model.Assentament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssentamentRepository extends JpaRepository<Assentament, Integer> {

    boolean existsByVertexId(Integer vertexId);

    @Query("select count(a) from Assentament a "
         + "where a.vertex.id in (select ar.vertexB.id from Aresta ar where ar.vertexA.id = :v) "
         + "   or a.vertex.id in (select ar.vertexA.id from Aresta ar where ar.vertexB.id = :v)")
    long countAdjacents(@Param("v") Integer vertexId);

    @Query("select count(a) from Assentament a where a.propietari.id = :j and a.vertex.id in (:va, :vb)")
    long countPropisEnVertexs(@Param("j") Integer jugadorId, @Param("va") Integer va, @Param("vb") Integer vb);

    @Query("select a from Assentament a where a.vertex.partida.id = :partida")
    List<Assentament> findByPartida(@Param("partida") Integer partidaId);
}
