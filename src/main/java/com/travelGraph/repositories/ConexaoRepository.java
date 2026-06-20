package com.travelGraph.repositories;

import com.travelGraph.entities.CityConnection;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ConexaoRepository extends Neo4jRepository<CityConnection, Long>, ConexaoRepositoryCustom {
    @Query("MATCH (origem:City)-[conn:CONECTA]->(destino:City) RETURN conn, origem, destino")
    List<CityConnection> findAllWithCities();

    @Query("MATCH (origem:City)-[conn:CONECTA]-(destino:City) WHERE ((id(origem) = $origemId AND id(destino) = $destinoId) OR (id(origem) = $destinoId AND id(destino) = $origemId)) RETURN conn, origem, destino LIMIT 1")
    Optional<CityConnection> findByOrigemAndDestino(@Param("origemId") Long origemId, @Param("destinoId") Long destinoId);

    @Query("MATCH (origem:City)-[conn:CONECTA]-(destino:City) WHERE ((id(origem) = $origemId AND id(destino) = $destinoId) OR (id(origem) = $destinoId AND id(destino) = $origemId)) RETURN conn.distancia LIMIT 1")
    Double findDistanceByOrigemAndDestino(@Param("origemId") Long origemId, @Param("destinoId") Long destinoId);

    @Query("MATCH (origem:City)-[conn:CONECTA]-(destino:City) WHERE ((id(origem) = $origemId AND id(destino) = $destinoId) OR (id(origem) = $destinoId AND id(destino) = $origemId)) RETURN conn.tempo LIMIT 1")
    Double findTimeByOrigemAndDestino(@Param("origemId") Long origemId, @Param("destinoId") Long destinoId);

    @Transactional
    @Query("MATCH ()-[conn:CONECTA]->() WHERE id(conn) = $id SET conn.distancia = $distance, conn.tempo = $time")
    void updateConnectionProperties(@Param("id") Long id, @Param("distance") Double distance, @Param("time") Double time);
}
