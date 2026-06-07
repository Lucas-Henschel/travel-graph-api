package com.travelGraph.authConfig;

import com.auth0.jwt.exceptions.JWTCreationException;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.travelGraph.dto.auth.CurrentUserDTO;
import com.travelGraph.entities.UserEntity;
import com.travelGraph.helpers.WriteErrorResponse;
import com.travelGraph.services.UserService;

import com.travelGraph.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Component
public class CustomFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private CurrentUserAuthentication currentUserAuthentication;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("token");

        if (tokenHeader != null) {
            try {
                Long userId = tokenService.validateToken(tokenHeader);
                UserEntity userEntity = userService.findById(userId);
    
                CurrentUserDTO currentUserEntityAuthentication = new CurrentUserDTO(
                    userEntity.getId(),
                    userEntity.getName(),
                    userEntity.getEmail()
                );
    
                currentUserAuthentication.setCurrentUserEntity(currentUserEntityAuthentication);
                SecurityContextHolder.getContext().setAuthentication(currentUserAuthentication);
            } catch (JWTDecodeException | JWTCreationException | ResponseStatusException ex) {
                WriteErrorResponse.writeErrorResponse(response, request, HttpStatus.FORBIDDEN, ex, objectMapper);
                return;
            } catch (ResourceNotFoundException ex) {
                WriteErrorResponse.writeErrorResponse(response, request, HttpStatus.NOT_FOUND, ex, objectMapper);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
