package com.coursparticuliers.service;

import com.coursparticuliers.dto.request.ProfileRequest;
import com.coursparticuliers.dto.response.UtilisateurResponse;
import com.coursparticuliers.entity.Eleve;
import com.coursparticuliers.entity.Professeur;
import com.coursparticuliers.entity.Utilisateur;
import com.coursparticuliers.entity.enums.Role;
import com.coursparticuliers.exception.BadRequestException;
import com.coursparticuliers.exception.ResourceNotFoundException;
import com.coursparticuliers.mapper.EntityMapper;
import com.coursparticuliers.repository.EleveRepository;
import com.coursparticuliers.repository.ProfesseurRepository;
import com.coursparticuliers.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UtilisateurRepository utilisateurRepository;
    private final ProfesseurRepository professeurRepository;
    private final EleveRepository eleveRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurResponse getProfile(Long userId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (utilisateur.getRole() == Role.PROFESSEUR) {
            return professeurRepository.findByUtilisateurId(userId)
                    .map(EntityMapper::toUtilisateurResponse)
                    .orElse(buildBasicResponse(utilisateur));
        }
        if (utilisateur.getRole() == Role.ELEVE) {
            return eleveRepository.findByUtilisateurId(userId)
                    .map(EntityMapper::toUtilisateurResponse)
                    .orElse(buildBasicResponse(utilisateur));
        }
        return EntityMapper.toUtilisateurResponse(utilisateur);
    }

    @Transactional
    public UtilisateurResponse updateProfile(Long userId, ProfileRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (!utilisateur.getEmail().equals(request.getEmail())
                && utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé");
        }

        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setTelephone(request.getTelephone());

        if (request.getMotDePasse() != null && !request.getMotDePasse().isBlank()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        }

        utilisateurRepository.save(utilisateur);

        if (utilisateur.getRole() == Role.PROFESSEUR) {
            Professeur professeur = professeurRepository.findByUtilisateurId(userId)
                    .orElseGet(() -> Professeur.builder().utilisateur(utilisateur).matieres(new ArrayList<>()).build());
            professeur.setDescription(request.getDescription());
            professeur.setTarifHoraire(request.getTarifHoraire());
            if (request.getPhotoProfil() != null) professeur.setPhotoProfil(request.getPhotoProfil());
            if (request.getMatieres() != null) professeur.setMatieres(request.getMatieres());
            professeurRepository.save(professeur);
        } else if (utilisateur.getRole() == Role.ELEVE) {
            Eleve eleve = eleveRepository.findByUtilisateurId(userId)
                    .orElseGet(() -> Eleve.builder().utilisateur(utilisateur).build());
            eleve.setNiveauScolaire(request.getNiveauScolaire());
            eleve.setAdresse(request.getAdresse());
            eleveRepository.save(eleve);
        }

        return getProfile(userId);
    }

    private UtilisateurResponse buildBasicResponse(Utilisateur utilisateur) {
        UtilisateurResponse response = EntityMapper.toUtilisateurResponse(utilisateur);
        if (utilisateur.getRole() == Role.PROFESSEUR) {
            professeurRepository.findByUtilisateurId(utilisateur.getId())
                    .ifPresent(p -> response.setProfesseurId(p.getId()));
        }
        if (utilisateur.getRole() == Role.ELEVE) {
            eleveRepository.findByUtilisateurId(utilisateur.getId())
                    .ifPresent(e -> response.setEleveId(e.getId()));
        }
        return response;
    }
}
