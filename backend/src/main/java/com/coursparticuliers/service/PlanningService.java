package com.coursparticuliers.service;

import com.coursparticuliers.dto.request.PlanningRequest;
import com.coursparticuliers.dto.response.PageResponse;
import com.coursparticuliers.dto.response.PlanningResponse;
import com.coursparticuliers.entity.Planning;
import com.coursparticuliers.entity.Professeur;
import com.coursparticuliers.exception.BadRequestException;
import com.coursparticuliers.exception.ResourceNotFoundException;
import com.coursparticuliers.mapper.EntityMapper;
import com.coursparticuliers.repository.PlanningRepository;
import com.coursparticuliers.repository.ProfesseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PlanningService {

    private final PlanningRepository planningRepository;
    private final ProfesseurRepository professeurRepository;

    public PageResponse<PlanningResponse> findByProfesseur(Long professeurId, Pageable pageable) {
        Page<PlanningResponse> page = planningRepository.findByProfesseurId(professeurId, pageable)
                .map(EntityMapper::toPlanningResponse);
        return PageResponse.from(page);
    }

    public PageResponse<PlanningResponse> findDisponibles(Long professeurId, LocalDate date, Pageable pageable) {
        Page<PlanningResponse> page = planningRepository.findDisponibles(professeurId, date, pageable)
                .map(EntityMapper::toPlanningResponse);
        return PageResponse.from(page);
    }

    public PlanningResponse findById(Long id) {
        Planning planning = planningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé"));
        return EntityMapper.toPlanningResponse(planning);
    }

    @Transactional
    public PlanningResponse create(PlanningRequest request) {
        validateHoraires(request);

        Professeur professeur = professeurRepository.findById(request.getProfesseurId())
                .orElseThrow(() -> new ResourceNotFoundException("Professeur non trouvé"));

        Planning planning = Planning.builder()
                .professeur(professeur)
                .date(request.getDate())
                .heureDebut(request.getHeureDebut())
                .heureFin(request.getHeureFin())
                .disponible(request.getDisponible() != null ? request.getDisponible() : true)
                .build();

        return EntityMapper.toPlanningResponse(planningRepository.save(planning));
    }

    @Transactional
    public PlanningResponse update(Long id, PlanningRequest request) {
        validateHoraires(request);

        Planning planning = planningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé"));

        Professeur professeur = professeurRepository.findById(request.getProfesseurId())
                .orElseThrow(() -> new ResourceNotFoundException("Professeur non trouvé"));

        planning.setProfesseur(professeur);
        planning.setDate(request.getDate());
        planning.setHeureDebut(request.getHeureDebut());
        planning.setHeureFin(request.getHeureFin());
        if (request.getDisponible() != null) planning.setDisponible(request.getDisponible());

        return EntityMapper.toPlanningResponse(planningRepository.save(planning));
    }

    @Transactional
    public void delete(Long id) {
        if (!planningRepository.existsById(id)) {
            throw new ResourceNotFoundException("Créneau non trouvé");
        }
        planningRepository.deleteById(id);
    }

    @Transactional
    public void marquerIndisponible(Long id) {
        Planning planning = planningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé"));
        planning.setDisponible(false);
        planningRepository.save(planning);
    }

    @Transactional
    public void libererCreneau(Long id) {
        Planning planning = planningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé"));
        planning.setDisponible(true);
        planningRepository.save(planning);
    }

    private void validateHoraires(PlanningRequest request) {
        if (!request.getHeureFin().isAfter(request.getHeureDebut())) {
            throw new BadRequestException("L'heure de fin doit être après l'heure de début");
        }
    }
}
