package com.costuras.soporte.controller;

import com.costuras.soporte.SoporteApplication; 
import com.costuras.soporte.dto.CrearTicketRequest;
import com.costuras.soporte.dto.ResponderTicketRequest;
import com.costuras.soporte.dto.TicketResponse;
import com.costuras.soporte.model.EstadoTicket;
import com.costuras.soporte.model.TipoTicket;
import com.costuras.soporte.security.UsuarioPrincipal;
import com.costuras.soporte.service.SoporteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
@WebMvcTest(SoporteController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = SoporteApplication.class)
class SoporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SoporteService soporteService;

    private UsuarioPrincipal principal;
    private TicketResponse ticketResponse;

    @BeforeEach
    void setUp() {
        principal = UsuarioPrincipal.builder()
                .id(1)
                .username("usuario1")
                .role("USER")
                .build();

        ticketResponse = new TicketResponse();
        ticketResponse.setId(1);
        ticketResponse.setAsunto("Problema con pedido");
        ticketResponse.setEstado(EstadoTicket.ABIERTO);
        ticketResponse.setUsernameUsuario("usuario1");
    }

    @Test
    void crearTicket_datosValidos_retorna201() throws Exception {
        CrearTicketRequest request = new CrearTicketRequest();
        request.setAsunto("Problema con pedido");
        request.setDescripcion("No llegó mi pedido");
        request.setTipo(TipoTicket.RECLAMO);

        when(soporteService.crearTicket(any(CrearTicketRequest.class), any(UsuarioPrincipal.class)))
                .thenReturn(ticketResponse);

        UsernamePasswordAuthenticationToken auth = 
                new UsernamePasswordAuthenticationToken(principal, null, List.of());

        mockMvc.perform(post("/soporte/tickets") 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(auth)) 
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticket.asunto").value("Problema con pedido"));
    }
@Test
    void getMisTickets_autenticado_retornaLista() throws Exception {
        when(soporteService.getMisTickets(any(UsuarioPrincipal.class))) 
                .thenReturn(List.of(ticketResponse));

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, List.of());

        
        mockMvc.perform(get("/soporte/tickets")
                .principal(auth)) 
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].asunto").value("Problema con pedido"));
    }

    @Test
    void getMiTicket_propio_retorna200() throws Exception {
        when(soporteService.getMiTicket(eq(1), any(UsuarioPrincipal.class))).thenReturn(ticketResponse);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, List.of());

        mockMvc.perform(get("/soporte/tickets/1")
                .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getMiTicket_ajeno_retorna403() throws Exception {
        when(soporteService.getMiTicket(eq(2), any(UsuarioPrincipal.class)))
                .thenThrow(new RuntimeException("Acceso denegado"));

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, List.of());

        mockMvc.perform(get("/soporte/tickets/2")
                .principal(auth))
               .andExpect(status().isNotFound());
    }

    @Test
    void getTodosTickets_admin_retornaLista() throws Exception {
        when(soporteService.getTodosTickets()).thenReturn(List.of(ticketResponse));

        mockMvc.perform(get("/soporte/admin/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getTodosTickets_filtradoPorEstado_retornaFiltrado() throws Exception {
        when(soporteService.getTicketsPorEstado(EstadoTicket.ABIERTO))
                .thenReturn(List.of(ticketResponse));

        mockMvc.perform(get("/soporte/admin/tickets").param("estado", "ABIERTO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("ABIERTO"));
    }

    @Test
    void responderTicket_admin_retorna200() throws Exception {
        ResponderTicketRequest request = new ResponderTicketRequest();
        request.setRespuesta("Revisamos tu caso");
        request.setEstado(EstadoTicket.CERRADO);

        ticketResponse.setEstado(EstadoTicket.CERRADO);
        ticketResponse.setRespuestaAdmin("Revisamos tu caso");

        when(soporteService.responderTicket(eq(1), any(ResponderTicketRequest.class))).thenReturn(ticketResponse);

        mockMvc.perform(put("/soporte/admin/tickets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Ticket actualizado correctamente"))
                .andExpect(jsonPath("$.ticket.estado").value("CERRADO"));
    }  
}