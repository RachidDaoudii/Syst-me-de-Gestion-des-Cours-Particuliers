package com.coursparticuliers.repository;

import com.coursparticuliers.entity.Seance;
import com.coursparticuliers.entity.enums.StatutSeance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SeanceRepository extends JpaRepository<Seance, Long> {

    Page<Seance> findByReservationEleveId(Long eleveId, Pageable pageable);

    Page<Seance> findByReservationProfesseurId(Long professeurId, Pageable pageable);

    @Query("SELECT s FROM Seance s WHERE s.statut = :statut " +
           "AND s.date = :date")
    List<Seance> findByStatutAndDate(@Param("statut") StatutSeance statut,
                                     @Param("date") LocalDate date);
}
