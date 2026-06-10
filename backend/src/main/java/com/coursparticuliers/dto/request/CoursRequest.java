package com.coursparticuliers.dto.request;

import com.coursparticuliers.entity.enums.StatutCours;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CoursRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String description;

    @NotBlank(message = "La matière est obligatoire")
    private String matiere;

    @NotBlank(message = "Le niveau est obligatoire")
    private String niveau;

    @NotNull(message = "Le prix est obligatoire")
    @Positive(message = "Le prix doit être positif")
    private BigDecimal prix;

    @NotNull(message = "L'identifiant du professeur est obligatoire")
    private Long professeurId;

    private StatutCours statut;
}
