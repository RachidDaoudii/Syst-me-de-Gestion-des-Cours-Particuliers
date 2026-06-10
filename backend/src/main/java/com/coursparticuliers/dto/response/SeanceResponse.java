package com.coursparticuliers.dto.response;

import com.coursparticuliers.entity.enums.StatutSeance;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class SeanceResponse {

    private Long id;
    private Long reservationId;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String compteRendu;
    private StatutSeance statut;
}
