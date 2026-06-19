package com.travelGraph.repositories;

import com.travelGraph.entities.CityNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface GraphQueryRepository extends Neo4jRepository<CityNode, Long> {
    /**
     * Caminho mais curto entre duas cidades
     */
    @Query("""
        MATCH (source:City), (target:City)
        WHERE id(source) = $sourceId AND id(target) = $targetId
        MATCH path = shortestPath((source)-[*]-(target))
        WITH nodes(path) AS nodes, relationships(path) AS rels
        RETURN nodes, rels
    """)
    List<Map<String, Object>> findShortestPath(
        @Param("sourceId") Long sourceId,
        @Param("targetId") Long targetId
    );

    /**
     * Cidades próximas dentro de um raio
     */
    @Query("""
        MATCH (source:City)-[connection:CONECTA]->(target:City)
        WHERE id(source) = $cityId AND connection.distancia <= $radius
        RETURN target,
           connection.distancia AS distance,
           connection.tempo AS duration
    """)
    List<Map<String, Object>> findNearbyLocations(
        @Param("cityId") Long cityId,
        @Param("radius") Double radius
    );

    /**
     * Atrações de uma cidade e cidades próximas dentro de um raio
     */
    @Query("""
        MATCH (attraction:Attraction)-[:PERTENCE_A]->(city:City)
        WHERE id(city) = $cityId
        RETURN attraction, 0.0 AS distance
        UNION
        MATCH (city:City)-[conn:CONECTA]->(nearby:City)<-[:PERTENCE_A]-(attraction:Attraction)
        WHERE id(city) = $cityId AND conn.distancia <= $radius
        RETURN attraction, conn.distancia AS distance
    """)
    List<Map<String, Object>> findNearbyAttractions(
        @Param("cityId") Long cityId,
        @Param("radius") Double radius
    );

    /**
     * Itinerário recomendado entre duas cidades
     */
    @Query("""
        MATCH (start:City), (end:City)
        WHERE id(start) = $startId AND id(end) = $endId
        MATCH path = shortestPath((start)-[*..10]-(end))
        RETURN nodes(path) AS nodes
    """)
    List<Map<String, Object>> findRecommendedItinerary(
        @Param("startId") Long startId,
        @Param("endId") Long endId
    );

    /**
     * Cria um grafo em memória para execução dos algoritmos GDS
     */
    @Query("""
        CALL gds.graph.project(
            $graphName,
            'City',
            'CONECTA',
            {
                relationshipProperties: {
                    weight: {
                        property: $weight,
                        defaultValue: 1.0
                    }
                }
            }
        )
        YIELD graphName
        RETURN graphName
    """)
    String createGraph(@Param("graphName") String graphName, @Param("weight") String weight);

    /**
     * Executa Dijkstra no grafo em memória
     */
    @Query("""
        CALL gds.shortestPath.dijkstra.stream(
            $graphName,
            {
                sourceNode: $origem,
                targetNode: $destino,
                relationshipWeightProperty: 'weight'
            }
        )
        YIELD nodeIds
        RETURN nodeIds
    """)
    List<Long> executeDijkstra(@Param("graphName") String graphName, @Param("origem") Long origem, @Param("destino") Long destino
    );

    /**
     * Remove o grafo em memória
     */
    @Query("CALL gds.graph.drop($graphName) YIELD graphName RETURN graphName")
    String dropGraph(@Param("graphName") String graphName);
}
