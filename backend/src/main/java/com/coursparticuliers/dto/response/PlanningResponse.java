package com.coursparticuliers.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class PlanningResponse {

    private Long id;
    private Long professeurId;
    private String professeurNom;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private boolean disponible;
}
