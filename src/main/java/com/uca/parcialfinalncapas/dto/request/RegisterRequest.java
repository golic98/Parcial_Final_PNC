package com.uca.parcialfinalncapas.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private Long id;
    private String nombre;
    private String correo;
    private String password;
    private String nombreRol;
}
