package com.coursparticuliers.controller;

import com.coursparticuliers.dto.request.ProfileRequest;
import com.coursparticuliers.dto.response.ApiResponse;
import com.coursparticuliers.dto.response.UtilisateurResponse;
import com.coursparticuliers.exception.UnauthorizedException;
import com.coursparticuliers.repository.UtilisateurRepository;
import com.coursparticuliers.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profil", description = "Gestion du profil utilisateur")
@SecurityRequirement(name = "Bearer Authentication")
public class ProfileController {

    private final ProfileService profileService;
    private final UtilisateurRepository utilisateurRepository;

    @GetMapping("/me")
    @Operation(summary = "Consulter mon profil")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> getMyProfile(Authentication authentication) {
        Long userId = resolveUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(profileService.getProfile(userId)));
    }

    @PutMapping("/me")
    @Operation(summary = "Modifier mon profil")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileRequest request) {
        Long userId = resolveUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                "Profil mis à jour", profileService.updateProfile(userId, request)));
    }

    private Long resolveUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Non authentifié");
        }
        return utilisateurRepository.findByEmail(authentication.getName())
                .map(u -> u.getId())
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));
    }
}
