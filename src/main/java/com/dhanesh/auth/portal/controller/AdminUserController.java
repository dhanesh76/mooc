package com.dhanesh.auth.portal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.dhanesh.auth.portal.entity.Users;
import com.dhanesh.auth.portal.service.AdminUserService;

import lombok.RequiredArgsConstructor;

/**
 * Controller for handling administrative user management actions
 * such as promoting users to admin, listing all users, and deleting users.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * Promotes a user to ADMIN role using the given username.
     *
     * @param username the username of the user to promote
     * @return 200 OK if successful, 400 Bad Request if user not found
     */
    @PutMapping("/make-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> promoteToAdmin(@RequestParam String username){
        boolean response = adminUserService.promoteToAdmin(username);
        return response
            ? ResponseEntity.ok().build()
            : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Not Found"); 
    }

    /**
     * Retrieves a list of all registered users.
     *
     * @return list of all users
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Users>> users(){
        return ResponseEntity.ok().body(adminUserService.getAllUsers());
    }

    /**
     * Deletes a user by their username.
     *
     * @param username the username of the user to delete
     * @return 204 No Content if successful, 400 Bad Request if user not found
     */
    @DeleteMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteUser(@RequestParam String username){
        boolean response = adminUserService.deleteUser(username);
        return response
            ? ResponseEntity.noContent().build()
            : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Not Found");
    }
}
