package com.dhanesh.auth.portal.controller;

import com.dhanesh.auth.portal.dto.AdminDashboardResponse;
import com.dhanesh.auth.portal.service.AdminDashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controller for providing admin dashboard data such as analytics,
 * statistics, and personalized insights for the logged-in administrator.
 * 
 * Only accessible by users with ROLE_ADMIN.
 */
@Tag(name = "Admin Dashboard", description = "Dashboard view and analytics for admin users")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    /**
     * Returns dashboard data for the currently authenticated admin.
     *
     * @param principal the authenticated user (admin)
     * @return dashboard response with summary statistics
     */
    @Operation(
        summary = "Get admin dashboard data",
        description = "Fetches platform-level stats like user count, course count, and feedback for the logged-in admin."
    )
    @GetMapping
    public ResponseEntity<AdminDashboardResponse> getDashboard(
        @Parameter(description = "Authenticated principal injected by Spring Security", hidden = true)
        Principal principal
    ) {
        return ResponseEntity.ok(dashboardService.getDashboard(principal.getName()));
    }
}
