package com.travelGraph.repositories;

import com.travelGraph.entities.CityNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends Neo4jRepository<CityNode, Long> {
    Optional<CityNode> findByName(String name);
}
