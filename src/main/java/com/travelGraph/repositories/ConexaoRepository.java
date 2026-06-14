package com.travelGraph.repositories;

import com.travelGraph.entities.CityConnection;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ConexaoRepository extends Neo4jRepository<CityConnection, Long> {
    @Query("MATCH (origem:City)-[conn:CONECTA]->(destino:City) RETURN conn, origem, destino")
    List<CityConnection> findAllWithCities();

    @Query("MATCH (origem:City)-[conn:CONECTA]->(destino:City) WHERE id(origem) = $origemId AND id(destino) = $destinoId RETURN conn, origem, destino")
    Optional<CityConnection> findByOrigemAndDestino(@Param("origemId") Long origemId, @Param("destinoId") Long destinoId);

    @Query("MATCH (origem:City)-[conn:CONECTA]->(destino:City) WHERE id(origem) = $origemId AND id(destino) = $destinoId RETURN conn.distancia")
    Double findDistanceByOrigemAndDestino(@Param("origemId") Long origemId, @Param("destinoId") Long destinoId);

    @Query("MATCH (origem:City)-[conn:CONECTA]->(destino:City) WHERE id(origem) = $origemId AND id(destino) = $destinoId RETURN conn.tempo")
    Double findTimeByOrigemAndDestino(@Param("origemId") Long origemId, @Param("destinoId") Long destinoId);

    @Query("""
        MATCH (origem:City)-[conn:CONECTA]->(destino:City)
        RETURN id(conn) AS connectionId,
           id(origem) AS originCityId, origem.name AS originCityName,
           id(destino) AS destinationCityId, destino.name AS destinationCityName,
           conn.distancia AS distance, conn.tempo AS time, conn.createdAt AS createdAt
    """)
    List<Map<String, Object>> findAllConnectionsWithCityInfo();

    @Query("""
        MATCH (origem:City)-[conn:CONECTA]->(destino:City)
        WHERE id(conn) = $id
        RETURN id(conn) AS connectionId,
           id(origem) AS originCityId, origem.name AS originCityName,
           id(destino) AS destinationCityId, destino.name AS destinationCityName,
           conn.distancia AS distance, conn.tempo AS time, conn.createdAt AS createdAt
    """)
    Optional<Map<String, Object>> findConnectionWithCityInfoById(@Param("id") Long id);
}
