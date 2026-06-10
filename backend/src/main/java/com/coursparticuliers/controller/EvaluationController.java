package com.coursparticuliers.controller;

import com.coursparticuliers.dto.request.EvaluationRequest;
import com.coursparticuliers.dto.response.ApiResponse;
import com.coursparticuliers.dto.response.EvaluationResponse;
import com.coursparticuliers.dto.response.PageResponse;
import com.coursparticuliers.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
@Tag(name = "Évaluations", description = "Gestion des évaluations")
@SecurityRequirement(name = "Bearer Authentication")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @GetMapping("/professeur/{professeurId}")
    @Operation(summary = "Évaluations d'un professeur")
    public ResponseEntity<ApiResponse<PageResponse<EvaluationResponse>>> findByProfesseur(
            @PathVariable Long professeurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                evaluationService.findByProfesseur(professeurId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date")))));
    }

    @GetMapping("/eleve/{eleveId}")
    @Operation(summary = "Évaluations données par un élève")
    public ResponseEntity<ApiResponse<PageResponse<EvaluationResponse>>> findByEleve(
            @PathVariable Long eleveId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                evaluationService.findByEleve(eleveId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date")))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'une évaluation")
    public ResponseEntity<ApiResponse<EvaluationResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(evaluationService.findById(id)));
    }

    @GetMapping("/professeur/{professeurId}/moyenne")
    @Operation(summary = "Note moyenne d'un professeur")
    public ResponseEntity<ApiResponse<Double>> getNoteMoyenne(@PathVariable Long professeurId) {
        return ResponseEntity.ok(ApiResponse.success(evaluationService.getNoteMoyenne(professeurId)));
    }

    @PostMapping
    @Operation(summary = "Ajouter une évaluation")
    public ResponseEntity<ApiResponse<EvaluationResponse>> create(@Valid @RequestBody EvaluationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Évaluation créée", evaluationService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une évaluation")
    public ResponseEntity<ApiResponse<EvaluationResponse>> update(
            @PathVariable Long id, @Valid @RequestBody EvaluationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(evaluationService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une évaluation")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        evaluationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Évaluation supprimée", null));
    }
}
