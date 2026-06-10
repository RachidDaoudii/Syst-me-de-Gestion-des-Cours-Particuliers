package com.coursparticuliers.service;

import com.coursparticuliers.dto.request.SeanceRequest;
import com.coursparticuliers.dto.response.PageResponse;
import com.coursparticuliers.dto.response.SeanceResponse;
import com.coursparticuliers.entity.Reservation;
import com.coursparticuliers.entity.Seance;
import com.coursparticuliers.entity.enums.StatutReservation;
import com.coursparticuliers.entity.enums.StatutSeance;
import com.coursparticuliers.exception.ResourceNotFoundException;
import com.coursparticuliers.mapper.EntityMapper;
import com.coursparticuliers.repository.ReservationRepository;
import com.coursparticuliers.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeanceService {

    private final SeanceRepository seanceRepository;
    private final ReservationRepository reservationRepository;

    public PageResponse<SeanceResponse> findByEleve(Long eleveId, Pageable pageable) {
        Page<SeanceResponse> page = seanceRepository.findByReservationEleveId(eleveId, pageable)
                .map(EntityMapper::toSeanceResponse);
        return PageResponse.from(page);
    }

    public PageResponse<SeanceResponse> findByProfesseur(Long professeurId, Pageable pageable) {
        Page<SeanceResponse> page = seanceRepository.findByReservationProfesseurId(professeurId, pageable)
                .map(EntityMapper::toSeanceResponse);
        return PageResponse.from(page);
    }

    public SeanceResponse findById(Long id) {
        Seance seance = seanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Séance non trouvée"));
        return EntityMapper.toSeanceResponse(seance);
    }

    @Transactional
    public SeanceResponse create(SeanceRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée"));

        Seance seance = Seance.builder()
                .reservation(reservation)
                .date(request.getDate())
                .heureDebut(request.getHeureDebut())
                .heureFin(request.getHeureFin())
                .compteRendu(request.getCompteRendu())
                .statut(request.getStatut() != null ? request.getStatut() : StatutSeance.PLANIFIEE)
                .build();

        return EntityMapper.toSeanceResponse(seanceRepository.save(seance));
    }

    @Transactional
    public SeanceResponse marquerRealisee(Long id, String compteRendu) {
        Seance seance = seanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Séance non trouvée"));

        seance.setStatut(StatutSeance.REALISEE);
        if (compteRendu != null) seance.setCompteRendu(compteRendu);

        Reservation reservation = seance.getReservation();
        reservation.setStatut(StatutReservation.TERMINEE);
        reservationRepository.save(reservation);

        return EntityMapper.toSeanceResponse(seanceRepository.save(seance));
    }

    @Transactional
    public SeanceResponse updateCompteRendu(Long id, String compteRendu) {
        Seance seance = seanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Séance non trouvée"));
        seance.setCompteRendu(compteRendu);
        return EntityMapper.toSeanceResponse(seanceRepository.save(seance));
    }
}
