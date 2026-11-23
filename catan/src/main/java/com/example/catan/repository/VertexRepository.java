package com.example.catan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.catan.model.Vertex;

public interface VertexRepository extends JpaRepository<Vertex, Integer> {
}
