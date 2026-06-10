package com.coursparticuliers.controller;

import com.coursparticuliers.dto.response.ApiResponse;
import com.coursparticuliers.dto.response.PageResponse;
import com.coursparticuliers.dto.response.UtilisateurResponse;
import com.coursparticuliers.entity.Eleve;
import com.coursparticuliers.exception.ResourceNotFoundException;
import com.coursparticuliers.mapper.EntityMapper;
import com.coursparticuliers.repository.EleveRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eleves")
@RequiredArgsConstructor
@Tag(name = "Élèves", description = "Consultation des élèves")
@SecurityRequirement(name = "Bearer Authentication")
public class EleveController {

    private final EleveRepository eleveRepository;

    @GetMapping
    @Operation(summary = "Liste des élèves")
    public ResponseEntity<ApiResponse<PageResponse<UtilisateurResponse>>> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String niveau,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UtilisateurResponse> result = eleveRepository
                .findByFilters(search, niveau, PageRequest.of(page, size))
                .map(EntityMapper::toUtilisateurResponse);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un élève")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> findById(@PathVariable Long id) {
        Eleve eleve = eleveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Élève non trouvé"));
        return ResponseEntity.ok(ApiResponse.success(EntityMapper.toUtilisateurResponse(eleve)));
    }
}
