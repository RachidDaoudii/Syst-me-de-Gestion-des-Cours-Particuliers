package com.coursparticuliers.dto.request;

import com.coursparticuliers.entity.enums.StatutSeance;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SeanceRequest {

    @NotNull(message = "L'identifiant de la réservation est obligatoire")
    private Long reservationId;

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime heureFin;

    private String compteRendu;

    private StatutSeance statut;
}
