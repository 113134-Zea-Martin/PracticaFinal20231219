package ar.edu.utn.frc.tup.lciv.dtos.reserva;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class requestPostDto {
    private Integer id_hotel;
    private String id_cliente;
    private String tipo_habitacion;
    private LocalDate fecha_ingreso;
    private LocalDate fecha_salida;
    private String medio_pago;
}
