package com.costuras.soporte.repository;

import com.costuras.soporte.model.EstadoTicket;
import com.costuras.soporte.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

   
    List<Ticket> findByIdUsuarioOrderByFechaCreacionDesc(Integer idUsuario);

    
    List<Ticket> findByEstadoOrderByFechaCreacionAsc(EstadoTicket estado);

    
    List<Ticket> findAllByOrderByFechaCreacionDesc();
}
