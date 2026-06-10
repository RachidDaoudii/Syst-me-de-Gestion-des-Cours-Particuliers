package com.coursparticuliers.service;

import com.coursparticuliers.entity.Reservation;
import com.coursparticuliers.entity.Seance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendReservationConfirmation(Reservation reservation) {
        String to = reservation.getEleve().getUtilisateur().getEmail();
        String subject = "Confirmation de réservation - Cours Particuliers";
        String body = String.format(
                "Bonjour %s,\n\nVotre réservation pour le cours \"%s\" avec %s %s a été enregistrée.\nStatut: %s\n\nCordialement,\nL'équipe Cours Particuliers",
                reservation.getEleve().getUtilisateur().getPrenom(),
                reservation.getCours().getTitre(),
                reservation.getProfesseur().getUtilisateur().getPrenom(),
                reservation.getProfesseur().getUtilisateur().getNom(),
                reservation.getStatut()
        );
        sendEmail(to, subject, body);
    }

    public void sendReservationCancellation(Reservation reservation) {
        String to = reservation.getEleve().getUtilisateur().getEmail();
        String subject = "Annulation de réservation - Cours Particuliers";
        String body = String.format(
                "Bonjour %s,\n\nVotre réservation pour le cours \"%s\" a été annulée.\n\nCordialement,\nL'équipe Cours Particuliers",
                reservation.getEleve().getUtilisateur().getPrenom(),
                reservation.getCours().getTitre()
        );
        sendEmail(to, subject, body);
    }

    public void sendSeanceReminder(Seance seance) {
        String to = seance.getReservation().getEleve().getUtilisateur().getEmail();
        String subject = "Rappel de séance - Cours Particuliers";
        String body = String.format(
                "Bonjour %s,\n\nRappel: vous avez une séance demain le %s de %s à %s.\n\nCordialement,\nL'équipe Cours Particuliers",
                seance.getReservation().getEleve().getUtilisateur().getPrenom(),
                seance.getDate(),
                seance.getHeureDebut(),
                seance.getHeureFin()
        );
        sendEmail(to, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("Impossible d'envoyer l'email à {}: {}", to, e.getMessage());
        }
    }
}
