package com.coursparticuliers.service;

import com.coursparticuliers.dto.request.ReservationRequest;
import com.coursparticuliers.dto.response.PageResponse;
import com.coursparticuliers.dto.response.ReservationResponse;
import com.coursparticuliers.entity.*;
import com.coursparticuliers.entity.enums.StatutReservation;
import com.coursparticuliers.entity.enums.StatutSeance;
import com.coursparticuliers.exception.BadRequestException;
import com.coursparticuliers.exception.ResourceNotFoundException;
import com.coursparticuliers.mapper.EntityMapper;
import com.coursparticuliers.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EleveRepository eleveRepository;
    private final CoursRepository coursRepository;
    private final PlanningRepository planningRepository;
    private final SeanceRepository seanceRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    public PageResponse<ReservationResponse> findAll(Long eleveId, Long professeurId,
                                                   StatutReservation statut, Pageable pageable) {
        Page<ReservationResponse> page = reservationRepository.findByFilters(eleveId, professeurId, statut, pageable)
                .map(EntityMapper::toReservationResponse);
        return PageResponse.from(page);
    }

    public ReservationResponse findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée"));
        return EntityMapper.toReservationResponse(reservation);
    }

    @Transactional
    public ReservationResponse create(ReservationRequest request) {
        Eleve eleve = eleveRepository.findById(request.getEleveId())
                .orElseThrow(() -> new ResourceNotFoundException("Élève non trouvé"));

        Cours cours = coursRepository.findById(request.getCoursId())
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));

        Planning planning = planningRepository.findById(request.getPlanningId())
                .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé"));

        if (!planning.isDisponible()) {
            throw new BadRequestException("Ce créneau n'est plus disponible");
        }

        if (!planning.getProfesseur().getId().equals(cours.getProfesseur().getId())) {
            throw new BadRequestException("Le créneau ne correspond pas au professeur du cours");
        }

        Reservation reservation = Reservation.builder()
                .eleve(eleve)
                .professeur(cours.getProfesseur())
                .cours(cours)
                .planning(planning)
                .statut(StatutReservation.EN_ATTENTE)
                .build();

        planning.setDisponible(false);
        planningRepository.save(planning);

        reservation = reservationRepository.save(reservation);

        Seance seance = Seance.builder()
                .reservation(reservation)
                .date(planning.getDate())
                .heureDebut(planning.getHeureDebut())
                .heureFin(planning.getHeureFin())
                .statut(StatutSeance.PLANIFIEE)
                .build();
        seanceRepository.save(seance);

        emailService.sendReservationConfirmation(reservation);
        notificationService.create(eleve.getUtilisateur().getId(),
                "Réservation créée pour le cours: " + cours.getTitre());
        notificationService.create(cours.getProfesseur().getUtilisateur().getId(),
                "Nouvelle réservation de " + eleve.getUtilisateur().getPrenom());

        return EntityMapper.toReservationResponse(reservation);
    }

    @Transactional
    public ReservationResponse confirmer(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée"));

        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservation = reservationRepository.save(reservation);

        notificationService.create(reservation.getEleve().getUtilisateur().getId(),
                "Votre réservation a été confirmée");

        return EntityMapper.toReservationResponse(reservation);
    }

    @Transactional
    public ReservationResponse annuler(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée"));

        if (reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new BadRequestException("Réservation déjà annulée");
        }

        reservation.setStatut(StatutReservation.ANNULEE);

        if (reservation.getPlanning() != null) {
            Planning planning = reservation.getPlanning();
            planning.setDisponible(true);
            planningRepository.save(planning);
        }

        reservation = reservationRepository.save(reservation);
        emailService.sendReservationCancellation(reservation);
        notificationService.create(reservation.getEleve().getUtilisateur().getId(),
                "Votre réservation a été annulée");

        return EntityMapper.toReservationResponse(reservation);
    }
}
