package com.coursparticuliers.controller;

import com.coursparticuliers.dto.response.ApiResponse;
import com.coursparticuliers.dto.response.PageResponse;
import com.coursparticuliers.dto.response.UtilisateurResponse;
import com.coursparticuliers.entity.Professeur;
import com.coursparticuliers.exception.ResourceNotFoundException;
import com.coursparticuliers.mapper.EntityMapper;
import com.coursparticuliers.repository.ProfesseurRepository;
import com.coursparticuliers.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/professeurs")
@RequiredArgsConstructor
@Tag(name = "Professeurs", description = "Consultation des professeurs")
public class ProfesseurController {

    private final ProfesseurRepository professeurRepository;
    private final EvaluationService evaluationService;

    @GetMapping
    @Operation(summary = "Liste des professeurs")
    public ResponseEntity<ApiResponse<PageResponse<UtilisateurResponse>>> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String matiere,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UtilisateurResponse> result = professeurRepository
                .findByFilters(search, matiere, PageRequest.of(page, size))
                .map(EntityMapper::toUtilisateurResponse);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un professeur")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> findById(@PathVariable Long id) {
        Professeur professeur = professeurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professeur non trouvé"));
        return ResponseEntity.ok(ApiResponse.success(EntityMapper.toUtilisateurResponse(professeur)));
    }

    @GetMapping("/{id}/note-moyenne")
    @Operation(summary = "Note moyenne d'un professeur")
    public ResponseEntity<ApiResponse<Double>> getNoteMoyenne(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(evaluationService.getNoteMoyenne(id)));
    }
}
