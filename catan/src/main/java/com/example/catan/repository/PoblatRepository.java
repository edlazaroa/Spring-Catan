package com.example.catan.repository;

import com.example.catan.model.Poblat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PoblatRepository extends JpaRepository<Poblat, Integer> {

    long countByPropietariId(Integer jugadorId);

    Optional<Poblat> findByVertexIdAndPropietariId(Integer vertexId, Integer jugadorId);

    /** Poblats propis en els vertexs donats que encara no tenen cap carretera propia adjacent (PREPARACIO). */
    @Query("select count(p) from Poblat p where p.propietari.id = :j and p.vertex.id in (:va, :vb) "
         + "and not exists (select c.id from Carretera c where c.propietari.id = :j "
         + "                and (c.aresta.vertexA.id = p.vertex.id or c.aresta.vertexB.id = p.vertex.id))")
    long countSenseCarretera(@Param("j") Integer jugadorId, @Param("va") Integer va, @Param("vb") Integer vb);

    /** Produccio: (propietari, terreny) per cada poblat adjacent a un hexagon amb el numero i sense lladre. */
    @Query("select p.propietari, hv.hexagon.terreny from Poblat p, HexagonVertex hv "
         + "where hv.vertex.id = p.vertex.id and hv.hexagon.partida.id = :partida "
         + "and hv.hexagon.numero = :numero and hv.hexagon.id <> :lladre")
    List<Object[]> produccio(@Param("partida") Integer partidaId, @Param("numero") Integer numero,
                             @Param("lladre") Integer lladreId);
}
