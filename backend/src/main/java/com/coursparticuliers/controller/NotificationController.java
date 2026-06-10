package com.coursparticuliers.controller;

import com.coursparticuliers.dto.response.ApiResponse;
import com.coursparticuliers.dto.response.NotificationResponse;
import com.coursparticuliers.dto.response.PageResponse;
import com.coursparticuliers.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notifications in-app")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/utilisateur/{utilisateurId}")
    @Operation(summary = "Notifications d'un utilisateur")
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getByUtilisateur(
            @PathVariable Long utilisateurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getByUtilisateur(utilisateurId, PageRequest.of(page, size))));
    }

    @GetMapping("/utilisateur/{utilisateurId}/non-lues")
    @Operation(summary = "Nombre de notifications non lues")
    public ResponseEntity<ApiResponse<Long>> countUnread(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.countUnread(utilisateurId)));
    }

    @PatchMapping("/{id}/lire")
    @Operation(summary = "Marquer une notification comme lue")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.markAsRead(id)));
    }
}
