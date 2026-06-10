package com.coursparticuliers.controller;

import com.coursparticuliers.dto.request.UtilisateurRequest;
import com.coursparticuliers.dto.response.ApiResponse;
import com.coursparticuliers.dto.response.PageResponse;
import com.coursparticuliers.dto.response.UtilisateurResponse;
import com.coursparticuliers.entity.enums.Role;
import com.coursparticuliers.service.UtilisateurService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Gestion des utilisateurs (Admin)")
@SecurityRequirement(name = "Bearer Authentication")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @GetMapping
    @Operation(summary = "Liste des utilisateurs avec pagination et filtres")
    public ResponseEntity<ApiResponse<PageResponse<UtilisateurResponse>>> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                utilisateurService.findAll(search, role, PageRequest.of(page, size, Sort.by("nom")))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un utilisateur")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(utilisateurService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Créer un utilisateur")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> create(@Valid @RequestBody UtilisateurRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Utilisateur créé", utilisateurService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un utilisateur")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> update(
            @PathVariable Long id, @Valid @RequestBody UtilisateurRequest request) {
        return ResponseEntity.ok(ApiResponse.success(utilisateurService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        utilisateurService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Utilisateur supprimé", null));
    }

    @PatchMapping("/{id}/toggle-actif")
    @Operation(summary = "Activer/Désactiver un compte")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> toggleActif(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(utilisateurService.toggleActif(id)));
    }
}
