package com.coursparticuliers.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EvaluationResponse {

    private Long id;
    private Long eleveId;
    private String eleveNom;
    private Long professeurId;
    private String professeurNom;
    private Integer note;
    private String commentaire;
    private LocalDateTime date;
}
