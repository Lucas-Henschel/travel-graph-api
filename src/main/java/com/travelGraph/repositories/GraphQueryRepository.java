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
        MATCH path = shortestPath(
            (source:City {id: $sourceId})-[*]-(target:City {id: $targetId})
        )
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
        MATCH (source:City {id: $cityId})-[connection:CONNECTS_TO]->(target:City)
        WHERE connection.distanceKm <= $radius
        RETURN target,
           connection.distanceKm AS distance,
           connection.durationMinutes AS duration,
           connection.transportType AS transport
    """)
    List<Map<String, Object>> findNearbyLocations(
        @Param("cityId") Long cityId,
        @Param("radius") Double radius
    );

    /**
     * Atrações de uma cidade e cidades próximas dentro de um raio
     */
    @Query("""
        MATCH (city:City {id: $cityId})-[:HAS_ATTRACTION]->(attraction:Attraction)
        RETURN attraction, 0.0 AS distance
        UNION
        MATCH (city:City {id: $cityId})-[conn:CONNECTS_TO]->(nearby:City)-[:HAS_ATTRACTION]->(attraction:Attraction)
        WHERE conn.distanceKm <= $radius
        RETURN attraction, conn.distanceKm AS distance
    """)
    List<Map<String, Object>> findNearbyAttractions(
        @Param("cityId") Long cityId,
        @Param("radius") Double radius
    );

    /**
     * Itinerário recomendado entre duas cidades
     */
    @Query("""
        MATCH path = shortestPath(
            (start:City {id: $startId})-[*..10]-(end:City {id: $endId})
        )
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
            'Cidade',
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
        YIELD graphName, nodeCount, relationshipCount
        RETURN graphName, nodeCount, relationshipCount
    """)
    Map<String, Object> createGraph(@Param("graphName") String graphName, @Param("weight") String weight);

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
        YIELD nodeIds, costs
        RETURN nodeIds, costs
    """)
    Map<String, Object> executeDijkstra(@Param("graphName") String graphName, @Param("origem") Long origem, @Param("destino") Long destino
    );

    /**
     * Remove o grafo em memória
     */
    @Query("CALL gds.graph.drop($graphName) YIELD graphName RETURN graphName")
    Map<String, Object> dropGraph(@Param("graphName") String graphName);
}
