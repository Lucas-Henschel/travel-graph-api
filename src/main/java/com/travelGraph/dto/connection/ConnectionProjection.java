package com.travelGraph.dto.connection;

import java.time.LocalDateTime;

public record ConnectionProjection(
    Long connectionId,
    Long originCityId,
    String originCityName,
    Long destinationCityId,
    String destinationCityName,
    Double distance,
    Double time,
    LocalDateTime createdAt
) {}
