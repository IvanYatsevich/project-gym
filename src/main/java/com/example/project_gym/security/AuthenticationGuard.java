package com.example.project_gym.security;

import org.springframework.stereotype.Component;

@Component
public class AuthenticationGuard {
    private final AuthorizationContext authContext;

    public AuthenticationGuard(AuthorizationContext authContext) {
        this.authContext = authContext;
    }

    public void requireAuthenticated() {
        if (!authContext.isAuthenticated()) {
            throw new SecurityException("Authentication required");
        }
    }
}
