package com.travelGraph.mapper;

import com.travelGraph.dto.city.CityResponseDTO;
import com.travelGraph.entities.CityNode;

public class CityMapper {
    public static CityResponseDTO toDTO(CityNode entity) {
        if (entity == null) {
            return null;
        }

        return new CityResponseDTO(
            entity.getId(),
            entity.getName(),
            entity.getLatitude(),
            entity.getLongitude(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static CityNode toEntity(CityResponseDTO dto) {
        if (dto == null) {
            return null;
        }

        CityNode entity = new CityNode();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }
}


