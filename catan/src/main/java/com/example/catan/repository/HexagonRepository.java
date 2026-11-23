package com.example.catan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.catan.model.Hexagon;

public interface HexagonRepository extends JpaRepository<Hexagon, Integer> {

    @Query("select h from Hexagon h where h.partida.partida_id = :id order by h.codi")
    List<Hexagon> findByPartidaIdOrderByCodi(@Param("id") Integer id);
}
