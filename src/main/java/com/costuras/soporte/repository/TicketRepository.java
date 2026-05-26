package com.costuras.soporte.repository;

import com.costuras.soporte.model.EstadoTicket;
import com.costuras.soporte.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    // Tickets del usuario autenticado
    List<Ticket> findByIdUsuarioOrderByFechaCreacionDesc(Integer idUsuario);

    // Para ADMIN: todos los tickets por estado
    List<Ticket> findByEstadoOrderByFechaCreacionAsc(EstadoTicket estado);

    // Para ADMIN: todos los tickets
    List<Ticket> findAllByOrderByFechaCreacionDesc();
}
