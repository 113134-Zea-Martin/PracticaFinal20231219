package ar.edu.utn.frc.tup.lciv.controllers;

import ar.edu.utn.frc.tup.lciv.dtos.reserva.requestPostDto;
import ar.edu.utn.frc.tup.lciv.models.Reserva;
import ar.edu.utn.frc.tup.lciv.services.impl.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reserva")
public class ReservaController {

    private final ReservaService reservaService;

    @Autowired
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<Reserva> createReserva(requestPostDto requestPostDto) {
        return ResponseEntity.ok(reservaService.createReserva(requestPostDto));
    }

    @GetMapping("/{id_reserva}")
    public ResponseEntity<Reserva> getReservaById(Integer id_reserva) {
        return ResponseEntity.ok(reservaService.getReservaById(id_reserva));
    }
}
