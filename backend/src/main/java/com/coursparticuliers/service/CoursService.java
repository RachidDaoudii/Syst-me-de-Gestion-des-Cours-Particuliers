package com.coursparticuliers.service;

import com.coursparticuliers.dto.request.CoursRequest;
import com.coursparticuliers.dto.response.CoursResponse;
import com.coursparticuliers.dto.response.PageResponse;
import com.coursparticuliers.entity.Cours;
import com.coursparticuliers.entity.Professeur;
import com.coursparticuliers.entity.enums.StatutCours;
import com.coursparticuliers.exception.ResourceNotFoundException;
import com.coursparticuliers.mapper.EntityMapper;
import com.coursparticuliers.repository.CoursRepository;
import com.coursparticuliers.repository.ProfesseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoursService {

    private final CoursRepository coursRepository;
    private final ProfesseurRepository professeurRepository;

    public PageResponse<CoursResponse> findAll(String matiere, String niveau, Long professeurId,
                                               StatutCours statut, String search, Pageable pageable) {
        Page<CoursResponse> page = coursRepository.findByFilters(matiere, niveau, professeurId, statut, search, pageable)
                .map(EntityMapper::toCoursResponse);
        return PageResponse.from(page);
    }

    public CoursResponse findById(Long id) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));
        return EntityMapper.toCoursResponse(cours);
    }

    @Transactional
    public CoursResponse create(CoursRequest request) {
        Professeur professeur = professeurRepository.findById(request.getProfesseurId())
                .orElseThrow(() -> new ResourceNotFoundException("Professeur non trouvé"));

        Cours cours = Cours.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .matiere(request.getMatiere())
                .niveau(request.getNiveau())
                .prix(request.getPrix())
                .professeur(professeur)
                .statut(request.getStatut() != null ? request.getStatut() : StatutCours.ACTIF)
                .build();

        return EntityMapper.toCoursResponse(coursRepository.save(cours));
    }

    @Transactional
    public CoursResponse update(Long id, CoursRequest request) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));

        Professeur professeur = professeurRepository.findById(request.getProfesseurId())
                .orElseThrow(() -> new ResourceNotFoundException("Professeur non trouvé"));

        cours.setTitre(request.getTitre());
        cours.setDescription(request.getDescription());
        cours.setMatiere(request.getMatiere());
        cours.setNiveau(request.getNiveau());
        cours.setPrix(request.getPrix());
        cours.setProfesseur(professeur);
        if (request.getStatut() != null) cours.setStatut(request.getStatut());

        return EntityMapper.toCoursResponse(coursRepository.save(cours));
    }

    @Transactional
    public void delete(Long id) {
        if (!coursRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cours non trouvé");
        }
        coursRepository.deleteById(id);
    }
}
