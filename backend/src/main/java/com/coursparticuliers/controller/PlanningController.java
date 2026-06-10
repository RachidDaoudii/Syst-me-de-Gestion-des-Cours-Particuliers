package com.coursparticuliers.controller;

import com.coursparticuliers.dto.request.PlanningRequest;
import com.coursparticuliers.dto.response.ApiResponse;
import com.coursparticuliers.dto.response.PageResponse;
import com.coursparticuliers.dto.response.PlanningResponse;
import com.coursparticuliers.service.PlanningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/plannings")
@RequiredArgsConstructor
@Tag(name = "Plannings", description = "Gestion des disponibilités")
public class PlanningController {

    private final PlanningService planningService;

    @GetMapping("/disponibles")
    @Operation(summary = "Créneaux disponibles")
    public ResponseEntity<ApiResponse<PageResponse<PlanningResponse>>> findDisponibles(
            @RequestParam(required = false) Long professeurId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                planningService.findDisponibles(professeurId, date,
                        PageRequest.of(page, size, Sort.by("date", "heureDebut")))));
    }

    @GetMapping("/professeur/{professeurId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Planning d'un professeur")
    public ResponseEntity<ApiResponse<PageResponse<PlanningResponse>>> findByProfesseur(
            @PathVariable Long professeurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                planningService.findByProfesseur(professeurId, PageRequest.of(page, size))));
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Détail d'un créneau")
    public ResponseEntity<ApiResponse<PlanningResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(planningService.findById(id)));
    }

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Créer un créneau de disponibilité")
    public ResponseEntity<ApiResponse<PlanningResponse>> create(@Valid @RequestBody PlanningRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Créneau créé", planningService.create(request)));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Modifier un créneau")
    public ResponseEntity<ApiResponse<PlanningResponse>> update(
            @PathVariable Long id, @Valid @RequestBody PlanningRequest request) {
        return ResponseEntity.ok(ApiResponse.success(planningService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Supprimer un créneau")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        planningService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Créneau supprimé", null));
    }
}
