package ar.edu.utn.frc.tup.lciv.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Disponibilidad {
    private String tipo_habitacion;
    private Integer hotel_id;
    private LocalDate fecha_desde;
    private LocalDate fecha_hasta;
    private boolean disponible;
}
