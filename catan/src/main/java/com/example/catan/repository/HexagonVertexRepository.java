package com.example.catan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.catan.model.HexagonVertex;

public interface HexagonVertexRepository extends JpaRepository<HexagonVertex, Integer> {

    @Query("select hv from HexagonVertex hv join fetch hv.hexagon hx join fetch hv.vertex "
         + "where hx.partida.partida_id = :id")
    List<HexagonVertex> findTaulerByPartida(@Param("id") Integer id);

    @Query("select hv from HexagonVertex hv join fetch hv.hexagon where hv.vertex.vertex_id = :id")
    List<HexagonVertex> findByVertex(@Param("id") Integer vertexId);
}
