package com.coursparticuliers.repository;

import com.coursparticuliers.entity.Utilisateur;
import com.coursparticuliers.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    @Query("SELECT u FROM Utilisateur u WHERE " +
           "(COALESCE(:search, '') = '' OR LOWER(u.nom) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:role IS NULL OR u.role = :role)")
    Page<Utilisateur> findByFilters(@Param("search") String search,
                                    @Param("role") Role role,
                                    Pageable pageable);
}
