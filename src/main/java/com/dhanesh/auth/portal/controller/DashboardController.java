package com.dhanesh.auth.portal.controller;

import com.dhanesh.auth.portal.dto.DashboardResponse;
import com.dhanesh.auth.portal.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/{userId}")
    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable String userId) {
        return ResponseEntity.ok(dashboardService.getDashboard(userId));
    }
}
