package com.coursparticuliers.repository;

import com.coursparticuliers.entity.Planning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlanningRepository extends JpaRepository<Planning, Long> {

    Page<Planning> findByProfesseurId(Long professeurId, Pageable pageable);

    List<Planning> findByProfesseurIdAndDisponibleTrueAndDateGreaterThanEqual(
            Long professeurId, LocalDate date);

    @Query("SELECT p FROM Planning p WHERE p.disponible = true " +
           "AND (:professeurId IS NULL OR p.professeur.id = :professeurId) " +
           "AND (:date IS NULL OR p.date = :date)")
    Page<Planning> findDisponibles(@Param("professeurId") Long professeurId,
                                   @Param("date") LocalDate date,
                                   Pageable pageable);
}
