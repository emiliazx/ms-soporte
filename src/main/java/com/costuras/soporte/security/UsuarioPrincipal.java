package com.costuras.soporte.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor

public class UsuarioPrincipal { 
    private Integer id; 
    private String username; 
    private String role;
 }
