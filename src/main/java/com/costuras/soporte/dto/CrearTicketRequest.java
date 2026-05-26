package com.costuras.soporte.dto;
import com.costuras.soporte.model.TipoTicket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data public class CrearTicketRequest {

    @NotBlank(message="El asunto es obligatorio") 

    private String asunto;

    @NotBlank(message="La descripción es obligatoria") 
    private String descripcion;
    
    @NotNull(message="El tipo es obligatorio (AYUDA, RECLAMO, SUGERENCIA)") 
    private TipoTicket tipo;
}
