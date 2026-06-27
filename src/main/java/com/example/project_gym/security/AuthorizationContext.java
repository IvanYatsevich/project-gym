package com.example.project_gym.security;

import com.example.project_gym.domain.entity.UserType;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AuthorizationContext {
    private String username;
    private UserType userType = UserType.UNKNOWN;
    private boolean authenticated = false;

    public void authenticate(String username, UserType userType) {
        this.username = username;
        this.userType = userType;
        this.authenticated = true;
    }

    public void clear() {
        this.username = null;
        this.userType = UserType.UNKNOWN;
        this.authenticated = false;
    }
}