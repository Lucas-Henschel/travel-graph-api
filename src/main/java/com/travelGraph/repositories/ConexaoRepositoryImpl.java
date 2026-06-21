package com.travelGraph.repositories;

import com.travelGraph.dto.connection.ConnectionProjection;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ConexaoRepositoryImpl implements ConexaoRepositoryCustom {
    private final Neo4jClient neo4jClient;

    public ConexaoRepositoryImpl(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    private static final String CONNECTION_QUERY = """
        MATCH (origem:City)-[conn:CONECTA]->(destino:City)
        %s
        RETURN id(conn) AS connectionId,
           id(origem) AS originCityId, origem.name AS originCityName,
           id(destino) AS destinationCityId, destino.name AS destinationCityName,
           conn.distancia AS distance, conn.tempo AS time, conn.createdAt AS createdAt
    """;

    @Override
    public List<ConnectionProjection> findAllConnectionsWithCityInfo() {
        return neo4jClient.query(CONNECTION_QUERY.formatted(""))
            .fetchAs(ConnectionProjection.class)
            .mappedBy((typeSystem, record) -> new ConnectionProjection(
                record.get("connectionId").asLong(),
                record.get("originCityId").asLong(),
                record.get("originCityName").asString(),
                record.get("destinationCityId").asLong(),
                record.get("destinationCityName").asString(),
                record.get("distance").isNull() ? null : record.get("distance").asDouble(),
                record.get("time").isNull() ? null : record.get("time").asDouble(),
                record.get("createdAt").isNull() ? null : record.get("createdAt").asLocalDateTime()
            ))
            .all()
            .stream()
            .toList();
    }

    @Override
    public Optional<ConnectionProjection> findConnectionWithCityInfoById(Long id) {
        return neo4jClient.query(CONNECTION_QUERY.formatted("WHERE id(conn) = $id"))
            .bind(id).to("id")
            .fetchAs(ConnectionProjection.class)
            .mappedBy((typeSystem, record) -> new ConnectionProjection(
                record.get("connectionId").asLong(),
                record.get("originCityId").asLong(),
                record.get("originCityName").asString(),
                record.get("destinationCityId").asLong(),
                record.get("destinationCityName").asString(),
                record.get("distance").isNull() ? null : record.get("distance").asDouble(),
                record.get("time").isNull() ? null : record.get("time").asDouble(),
                record.get("createdAt").isNull() ? null : record.get("createdAt").asLocalDateTime()
            ))
            .one();
    }
}
