package com.costuras.soporte.model;



import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;                  // extraído del token JWT

    @Column(name = "username_usuario", nullable = false)
    private String usernameUsuario;             // extraído del token JWT

    @Column(nullable = false)
    private String asunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTicket tipo;                    // AYUDA, RECLAMO, SUGERENCIA

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTicket estado;                // ABIERTO, EN_PROCESO, RESUELTO, CERRADO

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "respuesta_admin", columnDefinition = "TEXT")
    private String respuestaAdmin;              // el ADMIN responde desde aquí
}
