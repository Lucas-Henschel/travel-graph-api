package com.travelGraph.repositories;

import com.travelGraph.entities.AttractionNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttractionRepository extends Neo4jRepository<AttractionNode, Long> {
    @Query("MATCH (a:Attraction)-[:PERTENCE_A]->(c:City) WHERE id(c) = $cityId RETURN a")
    List<AttractionNode> findByCityId(@Param("cityId") Long cityId);

    @Query("MATCH (a:Attraction), (c:City) WHERE id(a) = $attractionId AND id(c) = $cityId " +
           "MERGE (a)-[:PERTENCE_A]->(c)")
    void linkToCity(@Param("attractionId") Long attractionId, @Param("cityId") Long cityId);

    @Transactional
    @Query("MATCH (a:Attraction) WHERE id(a) = $id " +
           "SET a.name = $name, a.description = $description, a.category = $category, " +
           "a.latitude = $latitude, a.longitude = $longitude, a.updatedAt = $updatedAt " +
           "RETURN a")
    AttractionNode updateProperties(@Param("id") Long id,
                                    @Param("name") String name,
                                    @Param("description") String description,
                                    @Param("category") String category,
                                    @Param("latitude") Double latitude,
                                    @Param("longitude") Double longitude,
                                    @Param("updatedAt") LocalDateTime updatedAt);
}
