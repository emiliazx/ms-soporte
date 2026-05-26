package com.costuras.soporte.dto;
import com.costuras.soporte.model.EstadoTicket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data public class ResponderTicketRequest {
    @NotBlank(message="La respuesta es obligatoria") 
    private String respuesta;
    
    @NotNull(message="El estado es obligatorio") 
    private EstadoTicket estado;
}
