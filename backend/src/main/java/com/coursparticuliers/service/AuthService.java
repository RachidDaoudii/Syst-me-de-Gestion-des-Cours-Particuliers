package com.coursparticuliers.service;

import com.coursparticuliers.dto.request.LoginRequest;
import com.coursparticuliers.dto.request.RefreshTokenRequest;
import com.coursparticuliers.dto.response.AuthResponse;
import com.coursparticuliers.entity.RefreshToken;
import com.coursparticuliers.entity.Utilisateur;
import com.coursparticuliers.entity.enums.Role;
import com.coursparticuliers.exception.UnauthorizedException;
import com.coursparticuliers.repository.EleveRepository;
import com.coursparticuliers.repository.ProfesseurRepository;
import com.coursparticuliers.repository.RefreshTokenRepository;
import com.coursparticuliers.repository.UtilisateurRepository;
import com.coursparticuliers.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;
    private final ProfesseurRepository professeurRepository;
    private final EleveRepository eleveRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse()));

        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Identifiants invalides"));

        if (!utilisateur.isActif()) {
            throw new UnauthorizedException("Compte désactivé");
        }

        String accessToken = jwtService.generateToken(utilisateur);
        RefreshToken refreshToken = upsertRefreshToken(utilisateur);

        return buildAuthResponse(utilisateur, accessToken, refreshToken.getToken());
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token invalide"));

        if (refreshToken.getDateExpiration().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token expiré");
        }

        Utilisateur utilisateur = refreshToken.getUtilisateur();
        String accessToken = jwtService.generateToken(utilisateur);

        return buildAuthResponse(utilisateur, accessToken, refreshToken.getToken());
    }

    private RefreshToken upsertRefreshToken(Utilisateur utilisateur) {
        String newToken = UUID.randomUUID().toString();
        Instant expiration = Instant.now().plusMillis(refreshExpirationMs);

        return refreshTokenRepository.findByUtilisateurId(utilisateur.getId())
                .map(existing -> {
                    existing.setToken(newToken);
                    existing.setDateExpiration(expiration);
                    return refreshTokenRepository.save(existing);
                })
                .orElseGet(() -> refreshTokenRepository.save(RefreshToken.builder()
                        .utilisateur(utilisateur)
                        .token(newToken)
                        .dateExpiration(expiration)
                        .build()));
    }

    private AuthResponse buildAuthResponse(Utilisateur utilisateur, String accessToken, String refreshToken) {
        Long professeurId = null;
        Long eleveId = null;

        if (utilisateur.getRole() == Role.PROFESSEUR) {
            professeurId = professeurRepository.findByUtilisateurId(utilisateur.getId())
                    .map(p -> p.getId()).orElse(null);
        } else if (utilisateur.getRole() == Role.ELEVE) {
            eleveId = eleveRepository.findByUtilisateurId(utilisateur.getId())
                    .map(e -> e.getId()).orElse(null);
        }

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(utilisateur.getId())
                .email(utilisateur.getEmail())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .role(utilisateur.getRole())
                .professeurId(professeurId)
                .eleveId(eleveId)
                .build();
    }
}
