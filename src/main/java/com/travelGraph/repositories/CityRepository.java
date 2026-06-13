package com.travelGraph.repositories;

import com.travelGraph.entities.CityNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends Neo4jRepository<CityNode, Long> {
    Optional<CityNode> findByName(String name);
}


