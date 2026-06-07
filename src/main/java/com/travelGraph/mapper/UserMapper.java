package com.travelGraph.mapper;

import com.travelGraph.dto.user.UserResponseDTO;
import com.travelGraph.entities.UserEntity;
import com.travelGraph.helpers.DateHelper;

public class UserMapper {
    public static UserResponseDTO toDTO(UserEntity entity) {
        if (entity == null) return null;

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());

        if (entity.getCreatedAt() != null) {
            dto.setCreatedAt(DateHelper.toIso8601(entity.getCreatedAt()));
        }

        return dto;
    }
}
