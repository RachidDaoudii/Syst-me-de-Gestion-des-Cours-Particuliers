package com.coursparticuliers.dto.response;

import com.coursparticuliers.entity.enums.StatutCours;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CoursResponse {

    private Long id;
    private String titre;
    private String description;
    private String matiere;
    private String niveau;
    private BigDecimal prix;
    private Long professeurId;
    private String professeurNom;
    private LocalDateTime dateCreation;
    private StatutCours statut;
}
