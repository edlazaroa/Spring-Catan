package com.example.catan.repository;

import com.example.catan.model.Ciutat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CiutatRepository extends JpaRepository<Ciutat, Integer> {

    long countByPropietariId(Integer jugadorId);

    @Query("select c.propietari, hv.hexagon.terreny from Ciutat c, HexagonVertex hv "
         + "where hv.vertex.id = c.vertex.id and hv.hexagon.partida.id = :partida "
         + "and hv.hexagon.numero = :numero and hv.hexagon.id <> :lladre")
    List<Object[]> produccio(@Param("partida") Integer partidaId, @Param("numero") Integer numero,
                             @Param("lladre") Integer lladreId);
}
