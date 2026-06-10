package com.coursparticuliers.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationRequest {

    @NotNull(message = "L'identifiant de l'élève est obligatoire")
    private Long eleveId;

    @NotNull(message = "L'identifiant du cours est obligatoire")
    private Long coursId;

    @NotNull(message = "L'identifiant du créneau est obligatoire")
    private Long planningId;
}
