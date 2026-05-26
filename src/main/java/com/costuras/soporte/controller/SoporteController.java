package com.costuras.soporte.controller;

import com.costuras.soporte.dto.CrearTicketRequest;
import com.costuras.soporte.dto.ResponderTicketRequest;
import com.costuras.soporte.dto.TicketResponse;
import com.costuras.soporte.model.EstadoTicket;
import com.costuras.soporte.security.UsuarioPrincipal;
import com.costuras.soporte.service.SoporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/soporte")
@RequiredArgsConstructor
public class SoporteController {

    private final SoporteService soporteService;

    @PostMapping("/tickets")
    public ResponseEntity<Map<String, Object>> crearTicket(
            @Valid @RequestBody CrearTicketRequest request,
            Authentication auth
    ) {
        UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
        TicketResponse ticket = soporteService.crearTicket(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Ticket creado correctamente", "ticket", ticket));
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<TicketResponse>> getMisTickets(Authentication auth) {
        UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
        return ResponseEntity.ok(soporteService.getMisTickets(principal));
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<TicketResponse> getMiTicket(
            @PathVariable Integer id,
            Authentication auth
    ) {
        UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
        return ResponseEntity.ok(soporteService.getMiTicket(id, principal));
    }

    @GetMapping("/admin/tickets")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<TicketResponse>> getTodosTickets(
            @RequestParam(required = false) EstadoTicket estado
    ) {
        if (estado != null) {
            return ResponseEntity.ok(soporteService.getTicketsPorEstado(estado));
        }
        return ResponseEntity.ok(soporteService.getTodosTickets());
    }

    @PutMapping("/admin/tickets/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> responderTicket(
            @PathVariable Integer id,
            @Valid @RequestBody ResponderTicketRequest request
    ) {
        TicketResponse ticket = soporteService.responderTicket(id, request);
        return ResponseEntity.ok(Map.of("mensaje", "Ticket actualizado correctamente", "ticket", ticket));
    }
}