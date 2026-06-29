package com.example.project_gym.security;

import com.example.project_gym.exception.UnauthenticatedException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationGuard {
    private final AuthorizationContext authContext;

    public AuthenticationGuard(AuthorizationContext authContext) {
        this.authContext = authContext;
    }

    public void requireAuthenticated() {
        if (!authContext.isAuthenticated()) {
            throw new UnauthenticatedException("Authentication required");
        }
    }
}
