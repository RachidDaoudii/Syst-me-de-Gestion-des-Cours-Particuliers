package com.coursparticuliers.service;

import com.coursparticuliers.dto.request.UtilisateurRequest;
import com.coursparticuliers.dto.response.PageResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final ProfesseurRepository professeurRepository;
    private final EleveRepository eleveRepository;
    private final PasswordEncoder passwordEncoder;

    public PageResponse<UtilisateurResponse> findAll(String search, Role role, Pageable pageable) {
        Page<UtilisateurResponse> page = utilisateurRepository.findByFilters(search, role, pageable)
                .map(EntityMapper::toUtilisateurResponse);
        return PageResponse.from(page);
    }

    public UtilisateurResponse findById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return EntityMapper.toUtilisateurResponse(utilisateur);
    }

    @Transactional
    public UtilisateurResponse create(UtilisateurRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé");
        }
        if (request.getMotDePasse() == null || request.getMotDePasse().isBlank()) {
            throw new BadRequestException("Le mot de passe est obligatoire");
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .telephone(request.getTelephone())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(request.getRole())
                .actif(true)
                .build();

        utilisateur = utilisateurRepository.save(utilisateur);
        createProfile(utilisateur, request);
        utilisateur = utilisateurRepository.findById(utilisateur.getId()).orElseThrow();
        return EntityMapper.toUtilisateurResponse(utilisateur);
    }

    @Transactional
    public UtilisateurResponse update(Long id, UtilisateurRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (!utilisateur.getEmail().equals(request.getEmail()) && utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé");
        }

        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setTelephone(request.getTelephone());
        if (request.getMotDePasse() != null && !request.getMotDePasse().isBlank()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        }

        updateProfile(utilisateur, request);
        utilisateur = utilisateurRepository.save(utilisateur);
        return EntityMapper.toUtilisateurResponse(utilisateur);
    }

    @Transactional
    public void delete(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur non trouvé");
        }
        utilisateurRepository.deleteById(id);
    }

    @Transactional
    public UtilisateurResponse toggleActif(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        utilisateur.setActif(!utilisateur.isActif());
        return EntityMapper.toUtilisateurResponse(utilisateurRepository.save(utilisateur));
    }

    private void createProfile(Utilisateur utilisateur, UtilisateurRequest request) {
        if (utilisateur.getRole() == Role.PROFESSEUR) {
            Professeur professeur = Professeur.builder()
                    .utilisateur(utilisateur)
                    .description(request.getDescription())
                    .tarifHoraire(request.getTarifHoraire())
                    .photoProfil(request.getPhotoProfil())
                    .matieres(request.getMatieres() != null ? request.getMatieres() : new ArrayList<>())
                    .build();
            professeurRepository.save(professeur);
        } else if (utilisateur.getRole() == Role.ELEVE) {
            Eleve eleve = Eleve.builder()
                    .utilisateur(utilisateur)
                    .niveauScolaire(request.getNiveauScolaire())
                    .adresse(request.getAdresse())
                    .build();
            eleveRepository.save(eleve);
        }
    }

    private void updateProfile(Utilisateur utilisateur, UtilisateurRequest request) {
        if (utilisateur.getRole() == Role.PROFESSEUR) {
            Professeur professeur = professeurRepository.findByUtilisateurId(utilisateur.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profil professeur non trouvé"));
            professeur.setDescription(request.getDescription());
            professeur.setTarifHoraire(request.getTarifHoraire());
            if (request.getPhotoProfil() != null) professeur.setPhotoProfil(request.getPhotoProfil());
            if (request.getMatieres() != null) professeur.setMatieres(request.getMatieres());
            professeurRepository.save(professeur);
        } else if (utilisateur.getRole() == Role.ELEVE) {
            Eleve eleve = eleveRepository.findByUtilisateurId(utilisateur.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profil élève non trouvé"));
            eleve.setNiveauScolaire(request.getNiveauScolaire());
            eleve.setAdresse(request.getAdresse());
            eleveRepository.save(eleve);
        }
    }
}
