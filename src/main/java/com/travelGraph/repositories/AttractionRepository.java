package com.travelGraph.repositories;

import com.travelGraph.entities.AttractionNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttractionRepository extends Neo4jRepository<AttractionNode, Long> {
    @Query("MATCH (a:Attraction)-[:PERTENCE_A]-(c:Cidade) WHERE c.id = $cityId RETURN a")
    List<AttractionNode> findByCityId(@Param("cityId") Long cityId);
}


