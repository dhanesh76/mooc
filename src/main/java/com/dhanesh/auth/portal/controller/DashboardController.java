package com.dhanesh.auth.portal.controller;

import com.dhanesh.auth.portal.dto.DashboardResponse;
import com.dhanesh.auth.portal.security.userdetails.UserPrincipal;
import com.dhanesh.auth.portal.service.DashboardService;
import lombok.RequiredArgsConstructor;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Returns student profile info, saved courses, and recommended courses for the dashboard.
     * Ensures user can only access their own dashboard.
     *
     * @param principal the authenticated user principal
     * @return dashboard response for the current user
     */
    
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DashboardResponse> getDashboard(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(dashboardService.getDashboard(principal.getUser().getId()));
    }
}

