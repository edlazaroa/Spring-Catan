package com.example.catan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.catan.model.Carretera;

public interface CarreteraRepository extends JpaRepository<Carretera, Integer> {

    @Query("select count(c) > 0 from Carretera c where c.aresta.aresta_id = :a")
    boolean existsByArestaId(@Param("a") Integer a);

    @Query("select count(c) from Carretera c where c.jugador.jugador_id = :j")
    long countByPropietariId(@Param("j") Integer j);

    /** Carreteres del jugador que toquen va o vb. */
    @Query("select count(c) from Carretera c where c.jugador.jugador_id = :j and "
         + "(c.aresta.vertexA.vertex_id in (:va, :vb) or c.aresta.vertexB.vertex_id in (:va, :vb))")
    long countConnectades(@Param("j") Integer j, @Param("va") Integer va, @Param("vb") Integer vb);

    @Query("select count(c) from Carretera c where c.jugador.jugador_id = :j and "
         + "(c.aresta.vertexA.vertex_id = :v or c.aresta.vertexB.vertex_id = :v)")
    long countAlVertex(@Param("j") Integer j, @Param("v") Integer v);
}
