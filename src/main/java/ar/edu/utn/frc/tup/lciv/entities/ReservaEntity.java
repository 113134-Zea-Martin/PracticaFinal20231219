package ar.edu.utn.frc.tup.lciv.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "reservas")
@NoArgsConstructor
public class ReservaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @Column(name = "id_cliente", nullable = false, length = 50)
    private String idCliente;

    @Column(name = "id_hotel", nullable = false)
    private Integer idHotel;

    @Column(name = "tipo_habitacion", nullable = false, length = 50)
    private String tipoHabitacion;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "estado_reserva", nullable = false, length = 20)
    private String estadoReserva;

    @Column(name = "precio", nullable = false)
    private Double precio;

    @Column(name = "medio_pago", nullable = false, length = 20)
    private String medioPago;
}
