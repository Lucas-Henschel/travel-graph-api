package com.travelGraph.authConfig;

import com.travelGraph.dto.auth.CurrentUserDTO;
import com.travelGraph.entities.UserNode;
import com.travelGraph.services.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CurrentUserAuthentication currentUserAuthentication;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login = authentication.getName();
        String password = (String) authentication.getCredentials();

        Optional<UserNode> userEntity = userService.findByEmail(login);

        if (userEntity.isPresent()){
            boolean isPasswordValid = passwordEncoder.matches(password, userEntity.get().getPassword());

            if (isPasswordValid){
                CurrentUserDTO currentUserEntityAuthentication = new CurrentUserDTO(
                    userEntity.get().getId(),
                    userEntity.get().getName(),
                    userEntity.get().getEmail()
                );

                currentUserAuthentication.setCurrentUserEntity(currentUserEntityAuthentication);
                return currentUserAuthentication;
            }
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
