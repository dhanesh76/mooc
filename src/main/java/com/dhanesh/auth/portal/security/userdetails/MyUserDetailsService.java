package com.dhanesh.auth.portal.security.userdetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dhanesh.auth.portal.entity.Users;
import com.dhanesh.auth.portal.exception.AuthenticationFailedException;
import com.dhanesh.auth.portal.model.AuthProvider;
import com.dhanesh.auth.portal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    /**
     * Loads user by either email or username. Used by Spring Security.
     * loginId : username or email 
     */

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
         
        Users user = userRepository
                        .findByUsernameOrEmail(loginId, loginId)
                        .orElseThrow(() -> 
                            new UsernameNotFoundException("User not found: " + loginId)
                        );
        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new AuthenticationFailedException("Please login using " + user.getAuthProvider().name().toLowerCase());
        }
        return new UserPrincipal(user);
    }
}