package com.dhanesh.auth.portal.controller;

import java.util.List;

import com.dhanesh.auth.portal.entity.Users;
import com.dhanesh.auth.portal.service.AdminUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling administrative user management actions
 * such as promoting users to admin, listing all users, and deleting users.
 * 
 * All endpoints in this controller require ROLE_ADMIN access.
 */
@Tag(name = "Admin User Management", description = "Admin-only endpoints for managing users")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * Promotes a user to ADMIN role using the given username.
     *
     * @param username the username of the user to promote
     * @return 200 OK if successful, 400 Bad Request if user not found
     */
    @Operation(
        summary = "Promote user to admin",
        description = "Promotes a regular user to admin by their username"
    )
    @PutMapping("/make-admin")
    public ResponseEntity<Object> promoteToAdmin(
        @RequestParam
        @Parameter(description = "Username of the user to promote", required = true)
        String username
    ) {
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
    @Operation(
        summary = "Get all users",
        description = "Returns a list of all registered users in the system"
    )
    @GetMapping("/users")
    public ResponseEntity<List<Users>> users() {
        return ResponseEntity.ok().body(adminUserService.getAllUsers());
    }

    /**
     * Deletes a user by their username.
     *
     * @param username the username of the user to delete
     * @return 204 No Content if successful, 400 Bad Request if user not found
     */
    @Operation(
        summary = "Delete user by username",
        description = "Deletes a user by their username"
    )
    @DeleteMapping("/users")
    public ResponseEntity<Object> deleteUser(
        @RequestParam
        @Parameter(description = "Username of the user to delete", required = true)
        String username
    ) {
        boolean response = adminUserService.deleteUser(username);
        return response
            ? ResponseEntity.noContent().build()
            : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Not Found");
    }
}
