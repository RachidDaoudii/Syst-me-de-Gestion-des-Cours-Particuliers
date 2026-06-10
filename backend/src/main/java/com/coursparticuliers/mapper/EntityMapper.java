package com.coursparticuliers.mapper;

import com.coursparticuliers.dto.response.*;
import com.coursparticuliers.entity.*;

public final class EntityMapper {

    private EntityMapper() {}

    public static UtilisateurResponse toUtilisateurResponse(Utilisateur u) {
        UtilisateurResponse.UtilisateurResponseBuilder builder = UtilisateurResponse.builder()
                .id(u.getId())
                .nom(u.getNom())
                .prenom(u.getPrenom())
                .email(u.getEmail())
                .telephone(u.getTelephone())
                .role(u.getRole())
                .actif(u.isActif())
                .dateCreation(u.getDateCreation());

        if (u.getProfesseur() != null) {
            builder.professeurId(u.getProfesseur().getId())
                    .matieres(u.getProfesseur().getMatieres())
                    .description(u.getProfesseur().getDescription())
                    .tarifHoraire(u.getProfesseur().getTarifHoraire())
                    .photoProfil(u.getProfesseur().getPhotoProfil());
        }
        if (u.getEleve() != null) {
            builder.eleveId(u.getEleve().getId())
                    .niveauScolaire(u.getEleve().getNiveauScolaire())
                    .adresse(u.getEleve().getAdresse());
        }
        return builder.build();
    }

    public static UtilisateurResponse toUtilisateurResponse(Professeur p) {
        UtilisateurResponse response = toUtilisateurResponse(p.getUtilisateur());
        response.setProfesseurId(p.getId());
        response.setMatieres(p.getMatieres());
        response.setDescription(p.getDescription());
        response.setTarifHoraire(p.getTarifHoraire());
        response.setPhotoProfil(p.getPhotoProfil());
        return response;
    }

    public static UtilisateurResponse toUtilisateurResponse(Eleve e) {
        UtilisateurResponse response = toUtilisateurResponse(e.getUtilisateur());
        response.setEleveId(e.getId());
        response.setNiveauScolaire(e.getNiveauScolaire());
        response.setAdresse(e.getAdresse());
        return response;
    }

    public static CoursResponse toCoursResponse(Cours c) {
        return CoursResponse.builder()
                .id(c.getId())
                .titre(c.getTitre())
                .description(c.getDescription())
                .matiere(c.getMatiere())
                .niveau(c.getNiveau())
                .prix(c.getPrix())
                .professeurId(c.getProfesseur().getId())
                .professeurNom(c.getProfesseur().getUtilisateur().getNom() + " " + c.getProfesseur().getUtilisateur().getPrenom())
                .dateCreation(c.getDateCreation())
                .statut(c.getStatut())
                .build();
    }

    public static PlanningResponse toPlanningResponse(Planning p) {
        return PlanningResponse.builder()
                .id(p.getId())
                .professeurId(p.getProfesseur().getId())
                .professeurNom(p.getProfesseur().getUtilisateur().getNom() + " " + p.getProfesseur().getUtilisateur().getPrenom())
                .date(p.getDate())
                .heureDebut(p.getHeureDebut())
                .heureFin(p.getHeureFin())
                .disponible(p.isDisponible())
                .build();
    }

    public static ReservationResponse toReservationResponse(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId())
                .eleveId(r.getEleve().getId())
                .eleveNom(r.getEleve().getUtilisateur().getNom() + " " + r.getEleve().getUtilisateur().getPrenom())
                .professeurId(r.getProfesseur().getId())
                .professeurNom(r.getProfesseur().getUtilisateur().getNom() + " " + r.getProfesseur().getUtilisateur().getPrenom())
                .coursId(r.getCours().getId())
                .coursTitre(r.getCours().getTitre())
                .planningId(r.getPlanning() != null ? r.getPlanning().getId() : null)
                .dateReservation(r.getDateReservation())
                .statut(r.getStatut())
                .build();
    }

    public static SeanceResponse toSeanceResponse(Seance s) {
        return SeanceResponse.builder()
                .id(s.getId())
                .reservationId(s.getReservation().getId())
                .date(s.getDate())
                .heureDebut(s.getHeureDebut())
                .heureFin(s.getHeureFin())
                .compteRendu(s.getCompteRendu())
                .statut(s.getStatut())
                .build();
    }

    public static EvaluationResponse toEvaluationResponse(Evaluation e) {
        return EvaluationResponse.builder()
                .id(e.getId())
                .eleveId(e.getEleve().getId())
                .eleveNom(e.getEleve().getUtilisateur().getNom() + " " + e.getEleve().getUtilisateur().getPrenom())
                .professeurId(e.getProfesseur().getId())
                .professeurNom(e.getProfesseur().getUtilisateur().getNom() + " " + e.getProfesseur().getUtilisateur().getPrenom())
                .note(e.getNote())
                .commentaire(e.getCommentaire())
                .date(e.getDate())
                .build();
    }

    public static NotificationResponse toNotificationResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .message(n.getMessage())
                .date(n.getDate())
                .lu(n.isLu())
                .build();
    }
}
