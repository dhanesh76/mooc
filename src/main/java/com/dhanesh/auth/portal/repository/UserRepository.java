package com.dhanesh.auth.portal.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dhanesh.auth.portal.entity.Users;



@Repository
public interface UserRepository extends MongoRepository<Users, String> {
    
    //used by the UserDetailsService 
    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email); 

    Optional<Users> findByUsernameOrEmail(String username, String email);
}