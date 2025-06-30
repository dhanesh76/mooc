package com.dhanesh.auth.portal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.dhanesh.auth.portal.entity.Users;
import com.dhanesh.auth.portal.service.AdminUserService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminUserController {

    private final AdminUserService adminUserService;

    
    @PutMapping("/make-admin")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Object> promoteToAdmin(@RequestParam String username){
        boolean response = adminUserService.promoteToAdmin(username);

        return response == true ? ResponseEntity.ok().build() : 
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Not Found"); 
    }


    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<List<Users>> users(){
        return ResponseEntity.ok().body(adminUserService.getAllusers());
    }

    @DeleteMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Object> deleteUser(@RequestParam String username){
        boolean response = adminUserService.deleteUser(username);
        
        return response == true ? ResponseEntity.noContent().build() : 
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Not Found");
    }
}