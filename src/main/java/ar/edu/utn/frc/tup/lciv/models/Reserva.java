package ar.edu.utn.frc.tup.lciv.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
        private Integer id_reserva;
        private String id_cliente;
        private Integer id_hotel;
        private String tipo_habitacion;
        private LocalDate fecha_ingreso;
        private LocalDate fecha_salida;
        private String estado_reserva;
        private Double precio;
        private String medio_pago;
}
