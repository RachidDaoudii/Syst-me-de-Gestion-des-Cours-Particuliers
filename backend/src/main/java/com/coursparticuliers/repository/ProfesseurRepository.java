package com.coursparticuliers.repository;

import com.coursparticuliers.entity.Professeur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfesseurRepository extends JpaRepository<Professeur, Long> {

    Optional<Professeur> findByUtilisateurId(Long utilisateurId);

    Optional<Professeur> findByUtilisateurEmail(String email);

    @Query("SELECT p FROM Professeur p JOIN p.utilisateur u WHERE " +
           "(COALESCE(:search, '') = '' OR LOWER(u.nom) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (COALESCE(:matiere, '') = '' OR :matiere MEMBER OF p.matieres)")
    Page<Professeur> findByFilters(@Param("search") String search,
                                   @Param("matiere") String matiere,
                                   Pageable pageable);
}
