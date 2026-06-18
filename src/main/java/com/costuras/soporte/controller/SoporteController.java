package com.costuras.soporte.controller;

import com.costuras.soporte.dto.CrearTicketRequest;
import com.costuras.soporte.dto.ResponderTicketRequest;
import com.costuras.soporte.dto.TicketResponse;
import com.costuras.soporte.model.EstadoTicket;
import com.costuras.soporte.security.UsuarioPrincipal;
import com.costuras.soporte.service.SoporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/soporte")
@RequiredArgsConstructor
@Tag(name = "Soporte", description = "Gestión de tickets de soporte al cliente")
public class SoporteController {

    private final SoporteService soporteService;

    @Operation(summary = "Crear ticket", description = "El usuario crea un nuevo ticket de soporte.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ticket creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping("/tickets")
    public ResponseEntity<Map<String, Object>> crearTicket(
            @Valid @RequestBody CrearTicketRequest request, Authentication auth) {
        UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
        TicketResponse ticket = soporteService.crearTicket(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Ticket creado correctamente", "ticket", ticket));
    }

    @Operation(summary = "Ver mis tickets", description = "Obtiene todos los tickets del usuario autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de tickets obtenida"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/tickets")
    public ResponseEntity<List<TicketResponse>> getMisTickets(Authentication auth) {
        UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
        return ResponseEntity.ok(soporteService.getMisTickets(principal));
    }

    @Operation(summary = "Ver ticket por ID", description = "Obtiene el detalle de un ticket propio.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ticket encontrado"),
        @ApiResponse(responseCode = "403", description = "No tienes permiso para ver este ticket"),
        @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
    })
    @GetMapping("/tickets/{id}")
    public ResponseEntity<TicketResponse> getMiTicket(
            @PathVariable Integer id, Authentication auth) {
        UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
        return ResponseEntity.ok(soporteService.getMiTicket(id, principal));
    }

    @Operation(summary = "Listar todos los tickets (ADMIN)",
               description = "Obtiene todos los tickets. Permite filtrar por estado. Requiere rol ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista completa de tickets"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/admin/tickets")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<TicketResponse>> getTodosTickets(
            @RequestParam(required = false) EstadoTicket estado) {
        if (estado != null) return ResponseEntity.ok(soporteService.getTicketsPorEstado(estado));
        return ResponseEntity.ok(soporteService.getTodosTickets());
    }

    @Operation(summary = "Responder ticket (ADMIN)",
               description = "El administrador responde y actualiza el estado de un ticket.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ticket actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping("/admin/tickets/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> responderTicket(
            @PathVariable Integer id, @Valid @RequestBody ResponderTicketRequest request) {
        TicketResponse ticket = soporteService.responderTicket(id, request);
        return ResponseEntity.ok(Map.of("mensaje", "Ticket actualizado correctamente", "ticket", ticket));
    }
}