package com.travelGraph.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.travelGraph.services.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.travelGraph.dto.auth.CurrentUserDTO;
import com.travelGraph.dto.user.CreateUserRequestDTO;
import com.travelGraph.dto.user.UpdateUserRequestDTO;
import com.travelGraph.dto.user.UserResponseDTO;
import com.travelGraph.entities.UserEntity;
import com.travelGraph.mapper.UserMapper;
import com.travelGraph.services.UserService;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * Lista todos os usuários
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        List<UserEntity> userEntities = userService.findAll();

        List<UserResponseDTO> listUserResponse = new ArrayList<>();

        for (UserEntity userEntity : userEntities) {
            listUserResponse.add(UserMapper.toDTO(userEntity));
        }

        return ResponseEntity.ok().body(listUserResponse);
    }

    /**
     * Busca um usuário por ID
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponseDTO> findById(@Valid @PathVariable String id) {
        Optional<UserEntity> userEntity = userService.findById(id);

        if (userEntity.isEmpty()) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        UserResponseDTO userResponse = UserMapper.toDTO(userEntity.get());

        return ResponseEntity.ok().body(userResponse);
    }

    /**
     * Cria um novo usuário
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody CreateUserRequestDTO createUserRequestDTO) {
        UserEntity userEntity = userService.create(createUserRequestDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(userEntity.getId()).toUri();

        UserResponseDTO userResponse = UserMapper.toDTO(userEntity);

        return ResponseEntity.created(uri).body(userResponse);
    }

    /**
     * Atualiza um usuário existente
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<UserResponseDTO> update(@Valid @PathVariable String id, @Valid @RequestBody UpdateUserRequestDTO updateUser) {
        UserEntity userEntity = userService.update(id, updateUser);
        UserResponseDTO userResponse = UserMapper.toDTO(userEntity);

        return ResponseEntity.ok().body(userResponse);
    }

    /**
     * Remove um usuário
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@Valid @PathVariable String id) {
        CurrentUserDTO currentUser = (CurrentUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        userService.delete(currentUser, id);
        return ResponseEntity.noContent().build();
    }
}
