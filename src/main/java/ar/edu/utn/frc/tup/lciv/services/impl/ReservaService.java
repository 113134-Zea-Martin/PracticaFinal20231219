package ar.edu.utn.frc.tup.lciv.services.impl;

import ar.edu.utn.frc.tup.lciv.dtos.Disponibilidad;
import ar.edu.utn.frc.tup.lciv.dtos.Precio;
import ar.edu.utn.frc.tup.lciv.dtos.reserva.requestPostDto;
import ar.edu.utn.frc.tup.lciv.entities.ReservaEntity;
import ar.edu.utn.frc.tup.lciv.models.Reserva;
import ar.edu.utn.frc.tup.lciv.repositories.ReservaRepository;
import ar.edu.utn.frc.tup.lciv.services.IApiExternaService;
import ar.edu.utn.frc.tup.lciv.services.IReservaService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static java.lang.Math.max;

@Service
public class ReservaService implements IReservaService {

    private final IApiExternaService apiExternaService;
    private final ReservaRepository reservaRepository;
    private static final Logger log = LoggerFactory.getLogger(ReservaService.class);

    @Autowired
    public ReservaService(IApiExternaService apiExternaService, ReservaRepository reservaRepository) {
        this.apiExternaService = apiExternaService;
        this.reservaRepository = reservaRepository;
    }

    @Override
    @Transactional
    public Reserva createReserva(requestPostDto requestPostDto) {

        if (!validarEntradas(requestPostDto)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos incorrectos");
        }

        boolean apiFallo = false;
        try {
            Disponibilidad disponibilidad = apiExternaService.getDisponibilidad(requestPostDto.getId_hotel(),
                    requestPostDto.getTipo_habitacion(), requestPostDto.getFecha_ingreso(), requestPostDto.getFecha_salida());
            if (!disponibilidad.isDisponible()) {
                throw new RuntimeException("No hay disponibilidad para la fecha seleccionada");
            }
        } catch (Exception e) {
            apiFallo = true;
        }

        Precio precio = apiExternaService.getPrecio(requestPostDto.getId_hotel(), requestPostDto.getTipo_habitacion());
        double precioTotal = calcularPrecio(precio, requestPostDto.getFecha_ingreso(), requestPostDto.getFecha_salida());
        double precioConDescuento = calcularDescuento(precioTotal, requestPostDto.getMedio_pago());
        double precioConDescuentoTipoCliente = calcularDescuentoTipoCliente(precioConDescuento,
                requestPostDto.getId_cliente(), requestPostDto.getFecha_ingreso());
        log.info("Precio total calculado para la estadía: {}", precioTotal);
        log.info("Precio con descuento aplicado por medio de pago ({}): {}", requestPostDto.getMedio_pago(), precioConDescuento);
        log.info("Precio final con descuento por tipo de cliente (ID Cliente: {}): {}", requestPostDto.getId_cliente(), precioConDescuentoTipoCliente);

        Reserva reserva = getReserva(requestPostDto, apiFallo, precioConDescuentoTipoCliente);
        ReservaEntity reservaEntity = mapToEntity(reserva);
        reservaRepository.save(reservaEntity);
        return mapToModel(reservaEntity);
    }

    @Override
    public Reserva getReservaById(Integer id_reserva) {
        ReservaEntity reservaEntity = reservaRepository.findById(id_reserva)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));
        return mapToModel(reservaEntity);
    }

    public ReservaEntity mapToEntity(Reserva reserva) {
        ReservaEntity reservaEntity = new ReservaEntity();
        reservaEntity.setIdCliente(reserva.getId_cliente());
        reservaEntity.setIdHotel(reserva.getId_hotel());
        reservaEntity.setTipoHabitacion(reserva.getTipo_habitacion());
        reservaEntity.setFechaIngreso(reserva.getFecha_ingreso());
        reservaEntity.setFechaSalida(reserva.getFecha_salida());
        reservaEntity.setEstadoReserva(reserva.getEstado_reserva());
        reservaEntity.setPrecio(reserva.getPrecio());
        reservaEntity.setMedioPago(reserva.getMedio_pago());
        return reservaEntity;
    }

    public Reserva mapToModel(ReservaEntity reservaEntity) {
        Reserva reserva = new Reserva();
        reserva.setId_reserva(reservaEntity.getIdReserva());
        reserva.setId_cliente(reservaEntity.getIdCliente());
        reserva.setId_hotel(reservaEntity.getIdHotel());
        reserva.setTipo_habitacion(reservaEntity.getTipoHabitacion());
        reserva.setFecha_ingreso(reservaEntity.getFechaIngreso());
        reserva.setFecha_salida(reservaEntity.getFechaSalida());
        reserva.setEstado_reserva(reservaEntity.getEstadoReserva());
        reserva.setPrecio(reservaEntity.getPrecio());
        reserva.setMedio_pago(reservaEntity.getMedioPago());
        return reserva;
    }

    public Reserva getReserva(requestPostDto requestPostDto, boolean apiFallo, double precioConDescuentoTipoCliente) {
        Reserva reserva = new Reserva();
        reserva.setId_cliente(requestPostDto.getId_cliente());
        reserva.setId_hotel(requestPostDto.getId_hotel());
        reserva.setTipo_habitacion(requestPostDto.getTipo_habitacion());
        reserva.setFecha_ingreso(requestPostDto.getFecha_ingreso());
        reserva.setFecha_salida(requestPostDto.getFecha_salida());
        if (apiFallo) {
            reserva.setEstado_reserva("PENDIENTE");
        } else {
            reserva.setEstado_reserva("EXITOSA");
        }
        reserva.setPrecio(precioConDescuentoTipoCliente);
        reserva.setMedio_pago(requestPostDto.getMedio_pago());
        return reserva;
    }

    public double calcularPrecio(Precio precio, LocalDate fecha_ingreso, LocalDate fecha_salida) {
        double precioTotal = 0;
        double precioPorDia = precio.getPrecio_lista();
        double precioPorDiaAlta = precioPorDia * 1.3;
        double precioPorDiaBaja = precioPorDia * 0.9;
        double precioPorDiaMedia = precio.getPrecio_lista();
        for (LocalDate fecha = fecha_ingreso; fecha.isBefore(fecha_salida.plusDays(1)); fecha = fecha.plusDays(1)) {
            if (fecha.getMonthValue() == 1 || fecha.getMonthValue() == 2 || fecha.getMonthValue() == 7 || fecha.getMonthValue() == 8) {
                precioPorDia = max(precioPorDiaAlta, precioPorDia);
            } else if (fecha.getMonthValue() == 6 || fecha.getMonthValue() == 9 || fecha.getMonthValue() == 12) {
                precioPorDia = max(precioPorDia, precioPorDiaMedia);
            } else {
                precioPorDia = max(precioPorDia, precioPorDiaBaja);
            }
        }
        for (LocalDate fecha = fecha_ingreso; fecha.isBefore(fecha_salida.plusDays(1)); fecha = fecha.plusDays(1)) {
            precioTotal += precioPorDia;
            log.info("Precio por día para la fecha {}: {}", fecha, precioPorDia);
        }
        return precioTotal;
    }

    public double calcularDescuento(double precioTotal, String medio_pago) {
        if (medio_pago.equals("EFECTIVO")) {
            return precioTotal * 0.75;
        } else if (medio_pago.equals("TARJETA_DEBITO")) {
            return precioTotal * 0.9;
        }
        return precioTotal;
    }

    public double calcularDescuentoTipoCliente(double precioTotal, String tipo_cliente, LocalDate fecha_ingreso) {
        if (tipo_cliente.equals("PASAPORTE")) {
            return precioTotal;
        } else if (tipo_cliente.equals("DNI")) {
            if (fecha_ingreso.getMonthValue() == 3 || fecha_ingreso.getMonthValue() == 4 ||
                    fecha_ingreso.getMonthValue() == 10 || fecha_ingreso.getMonthValue() == 11) {
                return precioTotal * 0.9;
            }
            return precioTotal;
        } else if (tipo_cliente.equals("CUIT")) {
            if (fecha_ingreso.getMonthValue() == 3 || fecha_ingreso.getMonthValue() == 4 ||
                    fecha_ingreso.getMonthValue() == 10 || fecha_ingreso.getMonthValue() == 11) {
                return precioTotal * 0.85;
            }
            return precioTotal * 0.9;
        }
        return precioTotal;
    }

    public boolean validarEntradas(requestPostDto requestPostDto) {
        if (requestPostDto.getFecha_ingreso().isBefore(LocalDate.now())) {
            return false;
        }
        if (requestPostDto.getFecha_salida().isBefore(requestPostDto.getFecha_ingreso())) {
            return false;
        }
        if (!requestPostDto.getTipo_habitacion().equals("SIMPLE") && !requestPostDto.getTipo_habitacion().equals("DOBLE") &&
                !requestPostDto.getTipo_habitacion().equals("TRIPLE")) {
            return false;
        }
        if (!requestPostDto.getId_cliente().equals("PASAPORTE") && !requestPostDto.getId_cliente().equals("DNI") &&
                !requestPostDto.getId_cliente().equals("CUIT")) {
            return false;
        }
        if (!requestPostDto.getMedio_pago().equals("EFECTIVO") && !requestPostDto.getMedio_pago().equals("TARJETA_DEBITO") &&
                !requestPostDto.getMedio_pago().equals("TARJETA_CREDITO")) {
            return false;
        }
        return true;
    }
}
