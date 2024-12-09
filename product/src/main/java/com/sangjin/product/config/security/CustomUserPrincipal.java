package com.sangjin.product.config.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomUserPrincipal {
    private String username;
    private String password;
    private Long userId;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserPrincipal(String username, String password, Long userId, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.userId = userId;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Long getUserId() {
        return userId;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}

