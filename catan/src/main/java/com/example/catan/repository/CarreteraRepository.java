package com.example.catan.repository;

import com.example.catan.model.Carretera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CarreteraRepository extends JpaRepository<Carretera, Integer> {

    long countByPropietariId(Integer jugadorId);

    boolean existsByArestaId(Integer arestaId);

    @Query("select count(c) from Carretera c where c.propietari.id = :j "
         + "and (c.aresta.vertexA.id in (:va, :vb) or c.aresta.vertexB.id in (:va, :vb))")
    long countConnectades(@Param("j") Integer jugadorId, @Param("va") Integer va, @Param("vb") Integer vb);

    @Query("select count(c) from Carretera c where c.propietari.id = :j "
         + "and (c.aresta.vertexA.id = :v or c.aresta.vertexB.id = :v)")
    long countAlVertex(@Param("j") Integer jugadorId, @Param("v") Integer vertexId);
}
