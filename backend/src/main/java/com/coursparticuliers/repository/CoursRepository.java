package com.coursparticuliers.repository;

import com.coursparticuliers.entity.Cours;
import com.coursparticuliers.entity.enums.StatutCours;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CoursRepository extends JpaRepository<Cours, Long> {

    @Query("SELECT c FROM Cours c WHERE " +
           "(COALESCE(:matiere, '') = '' OR LOWER(c.matiere) LIKE LOWER(CONCAT('%', :matiere, '%'))) " +
           "AND (COALESCE(:niveau, '') = '' OR LOWER(c.niveau) LIKE LOWER(CONCAT('%', :niveau, '%'))) " +
           "AND (:professeurId IS NULL OR c.professeur.id = :professeurId) " +
           "AND (:statut IS NULL OR c.statut = :statut) " +
           "AND (COALESCE(:search, '') = '' OR LOWER(c.titre) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Cours> findByFilters(@Param("matiere") String matiere,
                              @Param("niveau") String niveau,
                              @Param("professeurId") Long professeurId,
                              @Param("statut") StatutCours statut,
                              @Param("search") String search,
                              Pageable pageable);
}
