package com.dhanesh.auth.portal.controller;

import com.dhanesh.auth.portal.dto.AdminDashboardResponse;
import com.dhanesh.auth.portal.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping
    public ResponseEntity<AdminDashboardResponse> getDashboard(Principal principal) {
        return ResponseEntity.ok(dashboardService.getDashboard(principal.getName()));
    }
}
