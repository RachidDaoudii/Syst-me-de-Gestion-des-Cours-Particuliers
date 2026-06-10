package com.coursparticuliers.service;

import com.coursparticuliers.dto.response.*;
import com.coursparticuliers.entity.Professeur;
import com.coursparticuliers.entity.Reservation;
import com.coursparticuliers.entity.enums.Role;
import com.coursparticuliers.entity.enums.StatutReservation;
import com.coursparticuliers.exception.ResourceNotFoundException;
import com.coursparticuliers.mapper.EntityMapper;
import com.coursparticuliers.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UtilisateurRepository utilisateurRepository;
    private final CoursRepository coursRepository;
    private final ReservationRepository reservationRepository;
    private final ProfesseurRepository professeurRepository;
    private final EleveRepository eleveRepository;
    private final EvaluationRepository evaluationRepository;
    private final SeanceRepository seanceRepository;

    public DashboardResponse getAdminDashboard() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("EN_ATTENTE", reservationRepository.countByStatut(StatutReservation.EN_ATTENTE));
        stats.put("CONFIRMEE", reservationRepository.countByStatut(StatutReservation.CONFIRMEE));
        stats.put("ANNULEE", reservationRepository.countByStatut(StatutReservation.ANNULEE));
        stats.put("TERMINEE", reservationRepository.countByStatut(StatutReservation.TERMINEE));

        List<ReservationResponse> recentes = reservationRepository.findAll(PageRequest.of(0, 5))
                .map(EntityMapper::toReservationResponse).getContent();

        return DashboardResponse.builder()
                .nombreProfesseurs(utilisateurRepository.countByRole(Role.PROFESSEUR))
                .nombreEleves(utilisateurRepository.countByRole(Role.ELEVE))
                .nombreCours(coursRepository.count())
                .nombreReservations(reservationRepository.count())
                .reservationsEnAttente(stats.get("EN_ATTENTE"))
                .reservationsConfirmees(stats.get("CONFIRMEE"))
                .statistiquesParStatut(stats)
                .reservationsRecentes(recentes)
                .build();
    }

    public DashboardResponse getProfesseurDashboard(Long utilisateurId) {
        Professeur professeur = professeurRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil professeur non trouvé"));

        List<ReservationResponse> reservations = reservationRepository
                .findByProfesseurId(professeur.getId(), PageRequest.of(0, 10))
                .map(EntityMapper::toReservationResponse).getContent();

        List<SeanceResponse> seances = seanceRepository
                .findByReservationProfesseurId(professeur.getId(), PageRequest.of(0, 10))
                .map(EntityMapper::toSeanceResponse).getContent();

        List<EvaluationResponse> evaluations = evaluationRepository
                .findByProfesseurId(professeur.getId(), PageRequest.of(0, 5))
                .map(EntityMapper::toEvaluationResponse).getContent();

        BigDecimal revenus = reservations.stream()
                .filter(r -> r.getStatut() == StatutReservation.CONFIRMEE || r.getStatut() == StatutReservation.TERMINEE)
                .map(r -> reservationRepository.findById(r.getId()).map(Reservation::getCours).orElse(null))
                .filter(c -> c != null)
                .map(c -> c.getPrix())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Double noteMoyenne = evaluationRepository.findAverageNoteByProfesseurId(professeur.getId());

        return DashboardResponse.builder()
                .nombreReservations((long) reservations.size())
                .revenusEstimes(revenus)
                .noteMoyenne(noteMoyenne != null ? Math.round(noteMoyenne * 100.0) / 100.0 : 0.0)
                .reservationsRecentes(reservations)
                .seancesRecentes(seances)
                .evaluationsRecentes(evaluations)
                .build();
    }

    public DashboardResponse getEleveDashboard(Long utilisateurId) {
        var eleve = eleveRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil élève non trouvé"));

        List<ReservationResponse> reservations = reservationRepository
                .findByEleveId(eleve.getId(), PageRequest.of(0, 10))
                .map(EntityMapper::toReservationResponse).getContent();

        List<SeanceResponse> seances = seanceRepository
                .findByReservationEleveId(eleve.getId(), PageRequest.of(0, 10))
                .map(EntityMapper::toSeanceResponse).getContent();

        List<EvaluationResponse> evaluations = evaluationRepository
                .findByEleveId(eleve.getId(), PageRequest.of(0, 5))
                .map(EntityMapper::toEvaluationResponse).getContent();

        return DashboardResponse.builder()
                .nombreReservations((long) reservations.size())
                .reservationsRecentes(reservations)
                .seancesRecentes(seances)
                .evaluationsRecentes(evaluations)
                .build();
    }
}
