package com.coursparticuliers.controller;

import com.coursparticuliers.dto.response.ApiResponse;
import com.coursparticuliers.dto.response.DashboardResponse;
import com.coursparticuliers.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Tableau de bord", description = "Statistiques par rôle")
@SecurityRequirement(name = "Bearer Authentication")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @Operation(summary = "Tableau de bord administrateur")
    public ResponseEntity<ApiResponse<DashboardResponse>> getAdminDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getAdminDashboard()));
    }

    @GetMapping("/professeur/{utilisateurId}")
    @Operation(summary = "Tableau de bord professeur")
    public ResponseEntity<ApiResponse<DashboardResponse>> getProfesseurDashboard(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getProfesseurDashboard(utilisateurId)));
    }

    @GetMapping("/eleve/{utilisateurId}")
    @Operation(summary = "Tableau de bord élève")
    public ResponseEntity<ApiResponse<DashboardResponse>> getEleveDashboard(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getEleveDashboard(utilisateurId)));
    }
}
