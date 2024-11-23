package ar.edu.utn.frc.tup.lciv.services;

import ar.edu.utn.frc.tup.lciv.dtos.Disponibilidad;
import ar.edu.utn.frc.tup.lciv.dtos.Precio;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface IApiExternaService {
    Disponibilidad getDisponibilidad(Integer hotel_id, String tipo_habitacion, LocalDate fecha_desde, LocalDate fecha_hasta);
    Precio getPrecio(Integer hotel_id, String tipo_habitacion);
}
