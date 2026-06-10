package com.coursparticuliers.repository;

import com.coursparticuliers.entity.Eleve;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EleveRepository extends JpaRepository<Eleve, Long> {

    Optional<Eleve> findByUtilisateurId(Long utilisateurId);

    Optional<Eleve> findByUtilisateurEmail(String email);

    @Query("SELECT e FROM Eleve e JOIN e.utilisateur u WHERE " +
           "(COALESCE(:search, '') = '' OR LOWER(u.nom) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (COALESCE(:niveau, '') = '' OR e.niveauScolaire = :niveau)")
    Page<Eleve> findByFilters(@Param("search") String search,
                              @Param("niveau") String niveau,
                              Pageable pageable);
}
