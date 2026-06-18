package com.costuras.soporte.service;


import com.costuras.soporte.dto.CrearTicketRequest;
import com.costuras.soporte.dto.ResponderTicketRequest;
import com.costuras.soporte.dto.TicketResponse;
import com.costuras.soporte.model.EstadoTicket;
import com.costuras.soporte.model.Ticket;
import com.costuras.soporte.model.TipoTicket;
import com.costuras.soporte.repository.TicketRepository;
import com.costuras.soporte.security.UsuarioPrincipal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class SoporteServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private SoporteService soporteService;

    private UsuarioPrincipal principal;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        principal = mock(UsuarioPrincipal.class);
        when(principal.getId()).thenReturn(1);
        when(principal.getUsername()).thenReturn("usuario1");

        ticket = Ticket.builder()
                .id(1)
                .idUsuario(1)
                .usernameUsuario("usuario1")
                .asunto("Problema con pedido")
                .descripcion("No llegó mi pedido")
                .tipo(TipoTicket.RECLAMO)
                .estado(EstadoTicket.ABIERTO)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    @Test
    void crearTicket_guardaYRetornaResponse() {
        CrearTicketRequest request = new CrearTicketRequest();
        request.setAsunto("Problema con pedido");
        request.setDescripcion("No llegó mi pedido");
        request.setTipo(TipoTicket.RECLAMO);

        when(ticketRepository.save(any())).thenReturn(ticket);

        TicketResponse result = soporteService.crearTicket(request, principal);

        assertEquals("Problema con pedido", result.getAsunto());
        assertEquals(EstadoTicket.ABIERTO, result.getEstado());
        verify(ticketRepository).save(any());
    }

    @Test
    void getMisTickets_retornaListaDelUsuario() {
        when(ticketRepository.findByIdUsuarioOrderByFechaCreacionDesc(1))
                .thenReturn(List.of(ticket));

        List<TicketResponse> result = soporteService.getMisTickets(principal);

        assertEquals(1, result.size());
        assertEquals("usuario1", result.get(0).getUsernameUsuario());
    }

    @Test
    void getMiTicket_propietario_retornaTicket() {
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));

        TicketResponse result = soporteService.getMiTicket(1, principal);

        assertEquals(1, result.getId());
    }

    @Test
    void getMiTicket_otroUsuario_lanzaExcepcion() {
        UsuarioPrincipal otro = mock(UsuarioPrincipal.class);
        when(otro.getId()).thenReturn(99);
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));

        assertThrows(RuntimeException.class,
                () -> soporteService.getMiTicket(1, otro));
    }

    @Test
    void getMiTicket_noExistente_lanzaExcepcion() {
        when(ticketRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> soporteService.getMiTicket(99, principal));
    }

    @Test
    void getTodosTickets_retornaListaCompleta() {
        when(ticketRepository.findAllByOrderByFechaCreacionDesc()).thenReturn(List.of(ticket));

        List<TicketResponse> result = soporteService.getTodosTickets();

        assertEquals(1, result.size());
    }

    @Test
    void getTicketsPorEstado_retornaFiltrados() {
        when(ticketRepository.findByEstadoOrderByFechaCreacionAsc(EstadoTicket.ABIERTO))
                .thenReturn(List.of(ticket));

        List<TicketResponse> result = soporteService.getTicketsPorEstado(EstadoTicket.ABIERTO);

        assertEquals(1, result.size());
        assertEquals(EstadoTicket.ABIERTO, result.get(0).getEstado());
    }

    @Test
    void responderTicket_actualizaRespuestaYEstado() {
        ResponderTicketRequest request = new ResponderTicketRequest();
        request.setRespuesta("Revisamos tu caso");
        request.setEstado(EstadoTicket.CERRADO);

        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any())).thenReturn(ticket);

        TicketResponse result = soporteService.responderTicket(1, request);

        assertEquals("Revisamos tu caso", result.getRespuestaAdmin());
        assertEquals(EstadoTicket.CERRADO, result.getEstado());
    }
}

