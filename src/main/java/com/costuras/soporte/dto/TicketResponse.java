package com.costuras.soporte.dto;
import com.costuras.soporte.model.EstadoTicket;
import com.costuras.soporte.model.TipoTicket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder 
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {

    private Integer id;
    private Integer idUsuario; 
    private String usernameUsuario;
    private String asunto;
    private String descripcion; 
    private TipoTicket tipo;
    private EstadoTicket estado; 
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
     private String respuestaAdmin;
}
