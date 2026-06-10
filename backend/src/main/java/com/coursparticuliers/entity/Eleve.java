package com.coursparticuliers.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "eleves")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Eleve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    private Utilisateur utilisateur;

    @Column(name = "niveau_scolaire")
    private String niveauScolaire;

    private String adresse;

    @OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Evaluation> evaluations = new ArrayList<>();
}
