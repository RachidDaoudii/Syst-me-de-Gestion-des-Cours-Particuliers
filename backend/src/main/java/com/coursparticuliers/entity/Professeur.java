package com.coursparticuliers.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "professeurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Professeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    private Utilisateur utilisateur;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "tarif_horaire", precision = 10, scale = 2)
    private BigDecimal tarifHoraire;

    @Column(name = "photo_profil")
    private String photoProfil;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "professeur_matieres", joinColumns = @JoinColumn(name = "professeur_id"))
    @Column(name = "matiere")
    @Builder.Default
    private List<String> matieres = new ArrayList<>();

    @OneToMany(mappedBy = "professeur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Cours> cours = new ArrayList<>();

    @OneToMany(mappedBy = "professeur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Planning> plannings = new ArrayList<>();

    @OneToMany(mappedBy = "professeur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "professeur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Evaluation> evaluations = new ArrayList<>();
}
