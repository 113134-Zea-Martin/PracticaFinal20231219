package ar.edu.utn.frc.tup.lciv.services;

import ar.edu.utn.frc.tup.lciv.dtos.reserva.requestPostDto;
import ar.edu.utn.frc.tup.lciv.models.Reserva;
import org.springframework.stereotype.Service;

@Service
public interface IReservaService {
    Reserva createReserva(requestPostDto requestPostDto);
    Reserva getReservaById(Integer id_reserva);
}
