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

    /**
     * Promote a user to ADMIN role.
     * @param username username of the user
     * @return true if successful, false if user not found
     */
    public boolean promoteToAdmin(String username){
        Optional<Users> user = userRepository.findByUsername(username);
        user.ifPresent(u -> {
            u.setRole("ADMIN"); 
            userRepository.save(u);
        });
        return user.isPresent();
    }

    /**
     * Return all users in the system.
     */
    public List<Users> getAllUsers(){
        return userRepository.findAll();
    }

    /**
     * Delete user by username.
     * @return true if deleted, false if user not found
     */
    public boolean deleteUser(String username) {
        Optional<Users> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return true;
        }
        return false;
    }
}
