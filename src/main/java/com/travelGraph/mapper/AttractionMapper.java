package com.travelGraph.mapper;

import com.travelGraph.dto.attraction.AttractionResponseDTO;
import com.travelGraph.entities.AttractionNode;

public class AttractionMapper {
    public static AttractionResponseDTO toDTO(AttractionNode entity) {
        if (entity == null) {
            return null;
        }
        return new AttractionResponseDTO(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getCategory(),
            entity.getLatitude(),
            entity.getLongitude(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getCity() != null ? CityMapper.toDTO(entity.getCity()) : null
        );
    }

    public static AttractionNode toEntity(AttractionResponseDTO dto) {
        if (dto == null) {
            return null;
        }
        AttractionNode entity = new AttractionNode();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCategory(dto.getCategory());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        if (dto.getCity() != null) {
            entity.setCity(CityMapper.toEntity(dto.getCity()));
        }
        return entity;
    }
}


