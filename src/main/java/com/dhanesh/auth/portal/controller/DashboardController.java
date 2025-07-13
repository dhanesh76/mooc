package com.dhanesh.auth.portal.controller;

import com.dhanesh.auth.portal.dto.DashboardResponse;
import com.dhanesh.auth.portal.security.userdetails.UserPrincipal;
import com.dhanesh.auth.portal.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for the student dashboard.
 * Provides profile, saved courses, and recommendations.
 */
@Tag(name = "Student Dashboard", description = "Access dashboard with profile, saved courses, and recommendations")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Returns student dashboard data for the logged-in user.
     *
     * @param principal the authenticated user
     * @return dashboard response with user profile, saved courses, and recommendations
     */
    @Operation(
        summary = "Get user dashboard",
        description = "Returns dashboard info like profile, saved courses, and recommended courses for the current user"
    )
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DashboardResponse> getDashboard(
        @AuthenticationPrincipal 
        @Parameter(description = "Authenticated user") UserPrincipal principal
    ) {
        return ResponseEntity.ok(dashboardService.getDashboard(principal.getUser().getId()));
    }
}
