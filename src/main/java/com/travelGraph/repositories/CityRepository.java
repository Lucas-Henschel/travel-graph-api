package com.travelGraph.repositories;

import com.travelGraph.entities.CityNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CityRepository extends Neo4jRepository<CityNode, Long> {
    Optional<CityNode> findByName(String name);

    @Transactional
    @Query("MATCH (c:City) WHERE id(c) = $id " +
           "SET c.name = $name, c.latitude = $latitude, c.longitude = $longitude, c.updatedAt = $updatedAt " +
           "RETURN c")
    CityNode updateProperties(@Param("id") Long id,
                              @Param("name") String name,
                              @Param("latitude") Double latitude,
                              @Param("longitude") Double longitude,
                              @Param("updatedAt") LocalDateTime updatedAt);
}
