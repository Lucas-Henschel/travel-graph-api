package com.travelGraph.services;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.travelGraph.dto.auth.CurrentUserDTO;
import com.travelGraph.dto.user.CreateUserRequestDTO;
import com.travelGraph.dto.user.UpdateUserRequestDTO;
import com.travelGraph.entities.UserNode;
import com.travelGraph.helpers.UpdateValueHelper;
import com.travelGraph.repositories.UserRepository;
import com.travelGraph.services.exceptions.DatabaseException;
import com.travelGraph.services.exceptions.ResourceNotFoundException;
import com.travelGraph.services.exceptions.UnprocessableEntityException;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserNode> findAll() {
        return userRepository.findAll();
    }

    public Optional<UserNode> findById(String id) {
        Optional<UserNode> user = userRepository.findById(id);
        return user;
    }

    public Optional<UserNode> findByEmail(String email) {
        Optional<UserNode> user = userRepository.findByEmail(email);
        return user;
    }

    public void delete(CurrentUserDTO currentUser, String id) {
        try {
            if (currentUser.getId().equals(id)) {
                throw new DatabaseException("Usuário não pode deletar a si mesmo");
            }

            findById(id);
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public UserNode update(String id, UpdateUserRequestDTO updateUser) {
        Optional<UserNode> entity = findById(id);

        if (entity.isEmpty()) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        if (updateUser.getPassword().isEmpty()) {
            updateUser.setPassword(null);
        } else {
            String passwordEncryption = passwordEncoder.encode(updateUser.getPassword());
            updateUser.setPassword(passwordEncryption);
        }

        updateData(entity.get(), updateUser);

        return userRepository.save(entity.get());
    }

    private void updateData(UserNode entity, UpdateUserRequestDTO updateUser) {
        UpdateValueHelper.updateIfNotNull(entity::setName, updateUser.getName());
        UpdateValueHelper.updateIfNotNull(entity::setEmail, updateUser.getEmail());
        UpdateValueHelper.updateIfNotNull(entity::setPassword, updateUser.getPassword());
    }

    public UserNode create(@Valid CreateUserRequestDTO createUser) {
        Optional<UserNode> findUserByEmail = userRepository.findByEmail(createUser.getEmail());

        if (findUserByEmail.isPresent()) {
            throw new UnprocessableEntityException("Já existe um usuário com esse e-mail");
        }
        
        String passwordEncryption = passwordEncoder.encode(createUser.getPassword());

        UserNode user = new UserNode();
        user.setEmail(createUser.getEmail());
        user.setName(createUser.getName());
        user.setPassword(passwordEncryption);

        return userRepository.save(user);
    }
}
