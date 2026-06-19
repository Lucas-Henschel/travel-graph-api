package com.travelGraph.controller;

import com.travelGraph.authConfig.TokenService;
import com.travelGraph.dto.auth.CurrentUserDTO;
import com.travelGraph.dto.auth.LoginRequestDTO;
import com.travelGraph.dto.auth.LoginResponseDTO;
import com.travelGraph.services.AuthService;
import com.travelGraph.services.exceptions.UnprocessableEntityException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthService authService;

    /**
     * Faz login do usuário e retorna token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO data) {
        Authentication authentication = authService.validLogin(data.getEmail(), data.getPassword());

        if (authentication != null && authentication.isAuthenticated()) {
            CurrentUserDTO currentUser = (CurrentUserDTO) authentication.getPrincipal();

            String token = tokenService.generateToken(currentUser);
            return ResponseEntity.ok(new LoginResponseDTO(token, currentUser));
        }

        throw new UnprocessableEntityException("Credenciais inválidas");
    }

    /**
     * Faz logout do usuário
     */
    @PostMapping("/logout")
    public ResponseEntity<Boolean> logout(HttpServletRequest request, HttpServletResponse response) {
        Boolean isLoggedOut = authService.logout(request, response);
        return ResponseEntity.ok().body(isLoggedOut);
    }
}
