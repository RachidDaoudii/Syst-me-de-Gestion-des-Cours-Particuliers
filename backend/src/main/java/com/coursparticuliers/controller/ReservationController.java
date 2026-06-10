package com.coursparticuliers.controller;

import com.coursparticuliers.dto.request.ReservationRequest;
import com.coursparticuliers.dto.response.ApiResponse;
import com.coursparticuliers.dto.response.PageResponse;
import com.coursparticuliers.dto.response.ReservationResponse;
import com.coursparticuliers.entity.enums.StatutReservation;
import com.coursparticuliers.service.ReservationService;
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
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Réservations", description = "Gestion des réservations")
@SecurityRequirement(name = "Bearer Authentication")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @Operation(summary = "Liste des réservations avec filtres")
    public ResponseEntity<ApiResponse<PageResponse<ReservationResponse>>> findAll(
            @RequestParam(required = false) Long eleveId,
            @RequestParam(required = false) Long professeurId,
            @RequestParam(required = false) StatutReservation statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                reservationService.findAll(eleveId, professeurId, statut,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateReservation")))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'une réservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Réserver un cours")
    public ResponseEntity<ApiResponse<ReservationResponse>> create(@Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Réservation créée", reservationService.create(request)));
    }

    @PatchMapping("/{id}/confirmer")
    @Operation(summary = "Confirmer une réservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> confirmer(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.confirmer(id)));
    }

    @PatchMapping("/{id}/annuler")
    @Operation(summary = "Annuler une réservation")
    public ResponseEntity<ApiResponse<ReservationResponse>> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.annuler(id)));
    }
}
