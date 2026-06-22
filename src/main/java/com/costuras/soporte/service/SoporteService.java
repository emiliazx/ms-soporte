package com.costuras.soporte.service;

import com.costuras.soporte.dto.CrearTicketRequest;
import com.costuras.soporte.dto.ResponderTicketRequest;
import com.costuras.soporte.dto.TicketResponse;
import com.costuras.soporte.model.EstadoTicket;
import com.costuras.soporte.model.Ticket;
import com.costuras.soporte.repository.TicketRepository;
import com.costuras.soporte.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
public class SoporteService {

    private final TicketRepository ticketRepository;


    public TicketResponse crearTicket(CrearTicketRequest request, UsuarioPrincipal principal) {
        Ticket ticket = Ticket.builder()
                .idUsuario(principal.getId())
                .usernameUsuario(principal.getUsername())
                .asunto(request.getAsunto())
                .descripcion(request.getDescripcion())
                .tipo(request.getTipo())
                .estado(EstadoTicket.ABIERTO)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return toResponse(ticketRepository.save(ticket));
    }

  
    public List<TicketResponse> getMisTickets(UsuarioPrincipal principal) {
        return ticketRepository
                .findByIdUsuarioOrderByFechaCreacionDesc(principal.getId())
                .stream().map(this::toResponse).toList();
    }

  
    public TicketResponse getMiTicket(Integer id, UsuarioPrincipal principal) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado: " + id));
        if (!ticket.getIdUsuario().equals(principal.getId())) {
            throw new RuntimeException("No tienes permiso para ver este ticket");
        }
        return toResponse(ticket);
    }

 
    public List<TicketResponse> getTodosTickets() {
        return ticketRepository.findAllByOrderByFechaCreacionDesc()
                .stream().map(this::toResponse).toList();
    }


    public List<TicketResponse> getTicketsPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstadoOrderByFechaCreacionAsc(estado)
                .stream().map(this::toResponse).toList();
    }

  
    public TicketResponse responderTicket(Integer id, ResponderTicketRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado: " + id));

        ticket.setRespuestaAdmin(request.getRespuesta());
        ticket.setEstado(request.getEstado());
        ticket.setFechaActualizacion(LocalDateTime.now());

        return toResponse(ticketRepository.save(ticket));
    }

  
    private TicketResponse toResponse(Ticket t) {
        return TicketResponse.builder()
                .id(t.getId())
                .idUsuario(t.getIdUsuario())
                .usernameUsuario(t.getUsernameUsuario())
                .asunto(t.getAsunto())
                .descripcion(t.getDescripcion())
                .tipo(t.getTipo())
                .estado(t.getEstado())
                .fechaCreacion(t.getFechaCreacion())
                .fechaActualizacion(t.getFechaActualizacion())
                .respuestaAdmin(t.getRespuestaAdmin())
                .build();
    }
}
