package ar.edu.utn.frc.tup.lciv.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Precio {
    private Integer id_hotel;
    private String tipo_habitacion;
    private Double precio_lista;
}
