package com.coursparticuliers.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardResponse {

    private Long nombreProfesseurs;
    private Long nombreEleves;
    private Long nombreCours;
    private Long nombreReservations;
    private Long reservationsEnAttente;
    private Long reservationsConfirmees;
    private BigDecimal revenusEstimes;
    private Double noteMoyenne;
    private List<ReservationResponse> reservationsRecentes;
    private List<SeanceResponse> seancesRecentes;
    private List<EvaluationResponse> evaluationsRecentes;
    private Map<String, Long> statistiquesParStatut;
}
