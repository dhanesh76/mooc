package com.dhanesh.auth.portal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.dhanesh.auth.portal.entity.Users;
import com.dhanesh.auth.portal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserService {
    private final UserRepository userRepository;

    public boolean promoteToAdmin(String username){
        Optional<Users> user = userRepository.findByUsername(username);

        user.ifPresent(u -> {
                u.setRole("ADMIN"); 
                userRepository.save(u);
            });
        return user.isPresent();
    }

    public List<Users> getAllusers(){
        return userRepository.findAll();
    }
}
