package com.dhanesh.auth.portal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import com.dhanesh.auth.portal.entity.CourseFeedback;
import com.dhanesh.auth.portal.entity.Users;
import com.dhanesh.auth.portal.repository.CourseFeedbackRepository;
import com.dhanesh.auth.portal.repository.CourseShareRepository;
import com.dhanesh.auth.portal.repository.ProfilePhotoRepository;
import com.dhanesh.auth.portal.repository.SavedCourseRepository;
import com.dhanesh.auth.portal.repository.StudentProfileRepository;
import com.dhanesh.auth.portal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserService {

    private final UserRepository userRepository;
    private final CourseShareRepository courseShareRepository;
    private final ProfilePhotoRepository profilePhotoRepository;
    private final SavedCourseRepository savedCourseRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CourseFeedbackRepository feedbackRepository;

    
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
            String id = user.get().getId();
            
            userRepository.deleteById(id);
            profilePhotoRepository.deleteByUserId(id);
            studentProfileRepository.deleteById(id);

            savedCourseRepository.deleteByUserId(id);

            List<CourseFeedback> feedbackList = feedbackRepository.findByUserId(id);
            for (CourseFeedback f : feedbackList) {
                f.setAnonymous(true);
                f.setUserId(null);
                feedbackRepository.save(f);
            }

            courseShareRepository.deleteByUserId(id);
        }
        return false;
    }
}
