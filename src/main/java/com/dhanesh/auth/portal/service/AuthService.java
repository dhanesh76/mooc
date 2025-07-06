package com.dhanesh.auth.portal.service;

import java.time.Instant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dhanesh.auth.portal.dto.auth.*;
import com.dhanesh.auth.portal.dto.otp.OtpRequest;
import com.dhanesh.auth.portal.entity.AuthProvider;
import com.dhanesh.auth.portal.entity.Users;
import com.dhanesh.auth.portal.exception.AuthenticationFailedException;
import com.dhanesh.auth.portal.exception.EmailAlreadyInUseException;
import com.dhanesh.auth.portal.exception.EmailNotVerifiedException;
import com.dhanesh.auth.portal.exception.UsernameAlreadyTakenException;
import com.dhanesh.auth.portal.model.OtpPurpose;
import com.dhanesh.auth.portal.repository.UserRepository;
import com.dhanesh.auth.portal.security.jwt.JwtService;

import lombok.AllArgsConstructor;

@Service 
@AllArgsConstructor
public class AuthService {  

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    /**
     * Handles user registration:
     * - Validates uniqueness of email and username
     * - Saves user with encoded password and default role
     * - Sends OTP for email verification
     */
    public SignupResponse signup(SignupRequest credentials){
        String username = credentials.username();
        String email = credentials.email();
        String password = credentials.password();

        // Check for existing username/email
        validateUserUniqueness(username, email);

        // Create and persist new user
        Users user = new Users(); 
        
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        user.setVerified(false);
        user.setCreatedAt(Instant.now());
        user.setAuthProvider(AuthProvider.LOCAL);

        userRepo.save(user);

        // Send OTP for verification
        otpService.sendOtp(new OtpRequest(email, OtpPurpose.VERIFICATION));

        /**
         * Frontend should redirect to /verify-otp endpoint with:
         * {
         *   "email": "example@domain.com",
         *   "otp": "123456",
         *   "purpose": "VERIFICATION"
         * }
         */
        return new SignupResponse(username, email, 
            "Registered successfully. An OTP has been sent to your email for verification.", 
            user.getAuthProvider());
    }

    /**
     * Handles user login:
     * - Validates email/username existence
     * - Verifies email confirmation
     * - Authenticates credentials via AuthenticationManager
     * - Generates and returns JWT if successful
     */
    public SigninResponse signin(SigninRequest request) {
        Users user = userRepo
            .findByUsernameOrEmail(request.loginId(), request.loginId())
            .orElseThrow(() -> new AuthenticationFailedException("invalid credentials"));

        // If not verified, send OTP and instruct user to verify
        if (!user.isVerified()) {
            otpService.sendOtp(new OtpRequest(user.getEmail(), OtpPurpose.VERIFICATION));
            throw new EmailNotVerifiedException(user.getEmail(), 
                "Email is not verified. Please check your inbox.");
        }

        // Authenticate credentials
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getEmail(), request.password())
        );

        // Spring Security should already throw if invalid, but kept for completeness
        if (!authentication.isAuthenticated()) {
            throw new AuthenticationFailedException("Invalid email/username or password.");
        }

        // Generate token
        String token = jwtService.generateToken(user);

        return new SigninResponse(
            user.getId(),
            request.loginId(),
            user.getRole(),
            token,
            jwtService.extractExpiration(token),
            user.getAuthProvider()
        );
    }

    /**
     * Marks a user as verified after successful OTP validation.
     */
    public void markUserVerified(String email){
        Users user = userRepo.findByEmail(email)
            .orElseThrow(() -> 
                new AuthenticationFailedException("User not found with email: " + email));
        user.setVerified(true);
        userRepo.save(user);
    }

    /**
     * Updates the user's password after OTP validation (forgot/reset password).
     */
    public void resetPassword(ResetPasswordRequest request) {
        Users user = userRepo.findByEmail(request.email())
            .orElseThrow(() -> new AuthenticationFailedException("User not found"));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepo.save(user);
    }

    /**
     * Utility method to enforce unique username and email at signup.
     */
    private void validateUserUniqueness(String username, String email) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new EmailAlreadyInUseException("Email is already registered.");
        }
        if (userRepo.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyTakenException("Username is already taken.");
        }
    }
}
