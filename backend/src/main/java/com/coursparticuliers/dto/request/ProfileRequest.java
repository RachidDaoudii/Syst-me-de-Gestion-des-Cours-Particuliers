package com.coursparticuliers.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProfileRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    private String telephone;

    private String motDePasse;

    // Profil professeur
    private List<String> matieres;
    private String description;
    private BigDecimal tarifHoraire;
    private String photoProfil;

    // Profil élève
    private String niveauScolaire;
    private String adresse;
}
