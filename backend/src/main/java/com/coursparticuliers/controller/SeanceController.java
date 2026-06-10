package com.coursparticuliers.controller;

import com.coursparticuliers.dto.request.SeanceRequest;
import com.coursparticuliers.dto.response.ApiResponse;
import com.coursparticuliers.dto.response.PageResponse;
import com.coursparticuliers.dto.response.SeanceResponse;
import com.coursparticuliers.service.SeanceService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/seances")
@RequiredArgsConstructor
@Tag(name = "Séances", description = "Gestion des séances")
@SecurityRequirement(name = "Bearer Authentication")
public class SeanceController {

    private final SeanceService seanceService;

    @GetMapping("/eleve/{eleveId}")
    @Operation(summary = "Historique des séances d'un élève")
    public ResponseEntity<ApiResponse<PageResponse<SeanceResponse>>> findByEleve(
            @PathVariable Long eleveId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                seanceService.findByEleve(eleveId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date")))));
    }

    @GetMapping("/professeur/{professeurId}")
    @Operation(summary = "Séances d'un professeur")
    public ResponseEntity<ApiResponse<PageResponse<SeanceResponse>>> findByProfesseur(
            @PathVariable Long professeurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                seanceService.findByProfesseur(professeurId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date")))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'une séance")
    public ResponseEntity<ApiResponse<SeanceResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(seanceService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Créer une séance")
    public ResponseEntity<ApiResponse<SeanceResponse>> create(@Valid @RequestBody SeanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Séance créée", seanceService.create(request)));
    }

    @PatchMapping("/{id}/realiser")
    @Operation(summary = "Marquer une séance comme réalisée")
    public ResponseEntity<ApiResponse<SeanceResponse>> marquerRealisee(
            @PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String compteRendu = body != null ? body.get("compteRendu") : null;
        return ResponseEntity.ok(ApiResponse.success(seanceService.marquerRealisee(id, compteRendu)));
    }

    @PatchMapping("/{id}/compte-rendu")
    @Operation(summary = "Ajouter/modifier le compte-rendu")
    public ResponseEntity<ApiResponse<SeanceResponse>> updateCompteRendu(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.success(
                seanceService.updateCompteRendu(id, body.get("compteRendu"))));
    }
}
