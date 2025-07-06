package com.dhanesh.auth.portal.security.userdetails;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.dhanesh.auth.portal.entity.Users;


public class UserPrincipal implements UserDetails{
    
    protected final Users user;

    public UserPrincipal(Users user){
        this.user = user;
    }

    public Users getUser(){
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    /**
     * Returns the user's email as the unique identifier for authentication.
     * 
     * Rationale:
     * - Email is globally unique and stable across login types (manual/social).
     * - Used consistently as JWT subject (`sub`) for token generation and validation.
     * - Aligns with industry best practices to support scalable, multi-login systems.
     */
    @Override
    public String getUsername() {
        return user.getEmail(); // Using email as the principal identity
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isVerified();
    }
}
