package com.example.catan.repository;

import com.example.catan.model.Hexagon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HexagonRepository extends JpaRepository<Hexagon, Integer> {
    List<Hexagon> findByPartidaIdOrderByCodi(Integer partidaId);
}
