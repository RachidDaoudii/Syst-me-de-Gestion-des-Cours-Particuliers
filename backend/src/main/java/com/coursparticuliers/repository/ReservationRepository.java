package com.coursparticuliers.repository;

import com.coursparticuliers.entity.Reservation;
import com.coursparticuliers.entity.enums.StatutReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findByEleveId(Long eleveId, Pageable pageable);

    Page<Reservation> findByProfesseurId(Long professeurId, Pageable pageable);

    long countByStatut(StatutReservation statut);

    @Query("SELECT r FROM Reservation r WHERE " +
           "(:eleveId IS NULL OR r.eleve.id = :eleveId) " +
           "AND (:professeurId IS NULL OR r.professeur.id = :professeurId) " +
           "AND (:statut IS NULL OR r.statut = :statut)")
    Page<Reservation> findByFilters(@Param("eleveId") Long eleveId,
                                    @Param("professeurId") Long professeurId,
                                    @Param("statut") StatutReservation statut,
                                    Pageable pageable);
}
