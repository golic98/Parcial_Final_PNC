package com.uca.parcialfinalncapas.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String correo;
    private String password;
    private String token;
    private String refreshToken;
    private String role;
}
