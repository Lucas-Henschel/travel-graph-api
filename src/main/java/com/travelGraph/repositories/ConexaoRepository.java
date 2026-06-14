package com.travelGraph.repositories;

import com.travelGraph.entities.CityConnection;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConexaoRepository extends Neo4jRepository<CityConnection, Long> {
    @Query("MATCH (origem:Cidade)-[conn:CONECTA]->(destino:Cidade) RETURN conn, origem, destino")
    List<CityConnection> findAllWithCities();

    @Query("MATCH (origem:Cidade {id: $origemId})-[conn:CONECTA]->(destino:Cidade {id: $destinoId}) RETURN conn, origem, destino")
    Optional<CityConnection> findByOrigemAndDestino(@Param("origemId") Long origemId, @Param("destinoId") Long destinoId);

    @Query("MATCH (origem:Cidade {id: $origemId})-[conn:CONECTA]->(destino:Cidade {id: $destinoId}) RETURN conn.distancia")
    Double findDistanceByOrigemAndDestino(@Param("origemId") Long origemId, @Param("destinoId") Long destinoId);

    @Query("MATCH (origem:Cidade {id: $origemId})-[conn:CONECTA]->(destino:Cidade {id: $destinoId}) RETURN conn.tempo")
    Double findTimeByOrigemAndDestino(@Param("origemId") Long origemId, @Param("destinoId") Long destinoId);
}
