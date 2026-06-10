package com.coursparticuliers.dto.response;

import com.coursparticuliers.entity.enums.StatutReservation;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReservationResponse {

    private Long id;
    private Long eleveId;
    private String eleveNom;
    private Long professeurId;
    private String professeurNom;
    private Long coursId;
    private String coursTitre;
    private Long planningId;
    private LocalDateTime dateReservation;
    private StatutReservation statut;
}
