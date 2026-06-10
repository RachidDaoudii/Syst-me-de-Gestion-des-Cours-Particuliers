package com.coursparticuliers.repository;

import com.coursparticuliers.entity.Evaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    Page<Evaluation> findByProfesseurId(Long professeurId, Pageable pageable);

    Page<Evaluation> findByEleveId(Long eleveId, Pageable pageable);

    @Query("SELECT AVG(e.note) FROM Evaluation e WHERE e.professeur.id = :professeurId")
    Double findAverageNoteByProfesseurId(@Param("professeurId") Long professeurId);
}
