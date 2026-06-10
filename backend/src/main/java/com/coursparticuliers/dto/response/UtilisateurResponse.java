package com.coursparticuliers.dto.response;

import com.coursparticuliers.entity.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UtilisateurResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private Role role;
    private boolean actif;
    private LocalDateTime dateCreation;

    // Profil professeur
    private Long professeurId;
    private List<String> matieres;
    private String description;
    private BigDecimal tarifHoraire;
    private String photoProfil;

    // Profil élève
    private Long eleveId;
    private String niveauScolaire;
    private String adresse;
}
