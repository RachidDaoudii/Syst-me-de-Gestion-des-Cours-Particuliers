package com.coursparticuliers.controller;

import com.coursparticuliers.dto.request.CoursRequest;
import com.coursparticuliers.dto.response.ApiResponse;
import com.coursparticuliers.dto.response.CoursResponse;
import com.coursparticuliers.dto.response.PageResponse;
import com.coursparticuliers.entity.enums.StatutCours;
import com.coursparticuliers.service.CoursService;
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
@RequestMapping("/api/cours")
@RequiredArgsConstructor
@Tag(name = "Cours", description = "Gestion des cours")
public class CoursController {

    private final CoursService coursService;

    @GetMapping
    @Operation(summary = "Liste des cours avec recherche et filtres")
    public ResponseEntity<ApiResponse<PageResponse<CoursResponse>>> findAll(
            @RequestParam(required = false) String matiere,
            @RequestParam(required = false) String niveau,
            @RequestParam(required = false) Long professeurId,
            @RequestParam(required = false) StatutCours statut,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                coursService.findAll(matiere, niveau, professeurId, statut, search,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation")))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un cours")
    public ResponseEntity<ApiResponse<CoursResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(coursService.findById(id)));
    }

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Créer un cours")
    public ResponseEntity<ApiResponse<CoursResponse>> create(@Valid @RequestBody CoursRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cours créé", coursService.create(request)));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Modifier un cours")
    public ResponseEntity<ApiResponse<CoursResponse>> update(
            @PathVariable Long id, @Valid @RequestBody CoursRequest request) {
        return ResponseEntity.ok(ApiResponse.success(coursService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Supprimer un cours")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        coursService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Cours supprimé", null));
    }
}
