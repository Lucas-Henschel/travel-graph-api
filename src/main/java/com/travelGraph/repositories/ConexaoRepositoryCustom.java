package com.travelGraph.repositories;

import com.travelGraph.dto.connection.ConnectionProjection;

import java.util.List;
import java.util.Optional;

public interface ConexaoRepositoryCustom {
    List<ConnectionProjection> findAllConnectionsWithCityInfo();
    Optional<ConnectionProjection> findConnectionWithCityInfoById(Long id);
}
