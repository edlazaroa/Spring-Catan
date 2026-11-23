package com.example.catan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.catan.model.Poblat;

public interface PoblatRepository extends JpaRepository<Poblat, Integer> {

    @Query("select count(p) from Poblat p where p.jugador.jugador_id = :j")
    long countByPropietariId(@Param("j") Integer j);

    @Query("select p from Poblat p where p.vertex.vertex_id = :v and p.jugador.jugador_id = :j")
    Optional<Poblat> findByVertexIdAndPropietariId(@Param("v") Integer v, @Param("j") Integer j);

    /** Poblats del jugador a va/vb sense cap carretera propia adjacent (PREPARACIO). */
    @Query("select count(p) from Poblat p where p.jugador.jugador_id = :j "
         + "and p.vertex.vertex_id in (:va, :vb) "
         + "and not exists (select 1 from Carretera c where c.jugador = p.jugador "
         + "and (c.aresta.vertexA = p.vertex or c.aresta.vertexB = p.vertex))")
    long countSenseCarretera(@Param("j") Integer j, @Param("va") Integer va, @Param("vb") Integer vb);

    /** Files [Jugador, TipusTerreny] dels poblats adjacents a hexagons amb aquest numero i sense lladre. */
    @Query("select p.jugador, hx.terreny from HexagonVertex hv join hv.hexagon hx "
         + "join Poblat p on p.vertex = hv.vertex "
         + "where hx.partida.partida_id = :partida and hx.numero = :tirada "
         + "and hx.hexagon_id <> :lladre")
    List<Object[]> produccio(@Param("partida") Integer partida, @Param("tirada") Integer tirada,
                             @Param("lladre") Integer lladre);
}
