package com.coursparticuliers.service;

import com.coursparticuliers.dto.request.EvaluationRequest;
import com.coursparticuliers.dto.response.EvaluationResponse;
import com.coursparticuliers.dto.response.PageResponse;
import com.coursparticuliers.entity.Eleve;
import com.coursparticuliers.entity.Evaluation;
import com.coursparticuliers.entity.Professeur;
import com.coursparticuliers.exception.ResourceNotFoundException;
import com.coursparticuliers.mapper.EntityMapper;
import com.coursparticuliers.repository.EleveRepository;
import com.coursparticuliers.repository.EvaluationRepository;
import com.coursparticuliers.repository.ProfesseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final EleveRepository eleveRepository;
    private final ProfesseurRepository professeurRepository;

    public PageResponse<EvaluationResponse> findByProfesseur(Long professeurId, Pageable pageable) {
        Page<EvaluationResponse> page = evaluationRepository.findByProfesseurId(professeurId, pageable)
                .map(EntityMapper::toEvaluationResponse);
        return PageResponse.from(page);
    }

    public PageResponse<EvaluationResponse> findByEleve(Long eleveId, Pageable pageable) {
        Page<EvaluationResponse> page = evaluationRepository.findByEleveId(eleveId, pageable)
                .map(EntityMapper::toEvaluationResponse);
        return PageResponse.from(page);
    }

    public EvaluationResponse findById(Long id) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Évaluation non trouvée"));
        return EntityMapper.toEvaluationResponse(evaluation);
    }

    public Double getNoteMoyenne(Long professeurId) {
        Double moyenne = evaluationRepository.findAverageNoteByProfesseurId(professeurId);
        return moyenne != null ? Math.round(moyenne * 100.0) / 100.0 : 0.0;
    }

    @Transactional
    public EvaluationResponse create(EvaluationRequest request) {
        Eleve eleve = eleveRepository.findById(request.getEleveId())
                .orElseThrow(() -> new ResourceNotFoundException("Élève non trouvé"));

        Professeur professeur = professeurRepository.findById(request.getProfesseurId())
                .orElseThrow(() -> new ResourceNotFoundException("Professeur non trouvé"));

        Evaluation evaluation = Evaluation.builder()
                .eleve(eleve)
                .professeur(professeur)
                .note(request.getNote())
                .commentaire(request.getCommentaire())
                .build();

        return EntityMapper.toEvaluationResponse(evaluationRepository.save(evaluation));
    }

    @Transactional
    public EvaluationResponse update(Long id, EvaluationRequest request) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Évaluation non trouvée"));

        evaluation.setNote(request.getNote());
        evaluation.setCommentaire(request.getCommentaire());

        return EntityMapper.toEvaluationResponse(evaluationRepository.save(evaluation));
    }

    @Transactional
    public void delete(Long id) {
        if (!evaluationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Évaluation non trouvée");
        }
        evaluationRepository.deleteById(id);
    }
}
