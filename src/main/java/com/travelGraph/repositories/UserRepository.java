package com.travelGraph.repositories;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import com.travelGraph.entities.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends Neo4jRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}