package com.coursparticuliers.dto.response;

import com.coursparticuliers.entity.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long userId;
    private String email;
    private String nom;
    private String prenom;
    private Role role;
    private Long professeurId;
    private Long eleveId;
}
