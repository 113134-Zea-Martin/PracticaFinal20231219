package ar.edu.utn.frc.tup.lciv.services.impl;

import ar.edu.utn.frc.tup.lciv.dtos.Disponibilidad;
import ar.edu.utn.frc.tup.lciv.dtos.Precio;
import ar.edu.utn.frc.tup.lciv.dtos.reserva.requestPostDto;
import ar.edu.utn.frc.tup.lciv.entities.ReservaEntity;
import ar.edu.utn.frc.tup.lciv.models.Reserva;
import ar.edu.utn.frc.tup.lciv.repositories.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReservaServiceTest {

    @Mock
    private ApiExternaService apiExternaService;

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private ReservaService reservaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createReserva() {
        requestPostDto request = new requestPostDto(1, "DNI", "SIMPLE", LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), "EFECTIVO");
        Disponibilidad disponibilidad = new Disponibilidad("SIMPLE", 1, request.getFecha_ingreso(), request.getFecha_salida(), true);
        Precio precio = new Precio(1, "SIMPLE", 1000.0);

        when(apiExternaService.getDisponibilidad(anyInt(), anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(disponibilidad);
        when(apiExternaService.getPrecio(anyInt(), anyString()))
                .thenReturn(precio);

        Reserva result = reservaService.createReserva(request);

        assertNotNull(result);
        assertEquals(request.getId_hotel(), result.getId_hotel());
        verify(reservaRepository, times(1)).save(any(ReservaEntity.class));
    }

    @Test
    void createReserva_whenInvalidRequest_shouldThrowException() {
        requestPostDto invalidRequest = new requestPostDto(1, "INVALID", "INVALID", LocalDate.now().minusDays(1), LocalDate.now().plusDays(5), "INVALID");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> reservaService.createReserva(invalidRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void getReservaById() {
        Integer idReserva = 1;
        ReservaEntity entity = new ReservaEntity();
        entity.setIdReserva(idReserva);
        when(reservaRepository.findById(idReserva)).thenReturn(Optional.of(entity));

        Reserva result = reservaService.getReservaById(idReserva);

        assertNotNull(result);
        assertEquals(idReserva, result.getId_reserva());
    }

    @Test
    void getReservaById_notFound_shouldThrowException() {
        Integer idReserva = 1;
        when(reservaRepository.findById(idReserva)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> reservaService.getReservaById(idReserva));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void mapToEntity() {
        Reserva reserva = new Reserva(1, "DNI", 1, "SIMPLE", LocalDate.now(), LocalDate.now().plusDays(5), "EXITOSA", 1000.0, "EFECTIVO");

        ReservaEntity result = reservaService.mapToEntity(reserva);

        assertNotNull(result);
        assertEquals(reserva.getId_cliente(), result.getIdCliente());
    }

    @Test
    void mapToModel() {
        ReservaEntity entity = new ReservaEntity();
        entity.setIdReserva(1);

        Reserva result = reservaService.mapToModel(entity);

        assertNotNull(result);
        assertEquals(entity.getIdReserva(), result.getId_reserva());
    }

    @Test
    void getReserva() {
        requestPostDto request = new requestPostDto(1, "DNI", "SIMPLE", LocalDate.now(), LocalDate.now().plusDays(5), "EFECTIVO");

        Reserva result = reservaService.getReserva(request, false, 900.0);

        assertNotNull(result);
        assertEquals("EXITOSA", result.getEstado_reserva());
    }

    @Test
    void calcularPrecio() {
        Precio precio = new Precio(1, "SIMPLE", 1000.0);
        LocalDate inicio = LocalDate.now();
        LocalDate fin = LocalDate.now().plusDays(3);

        double result = reservaService.calcularPrecio(precio, inicio, fin);

        assertEquals(4000.0, result);
    }

    @Test
    void calcularDescuento() {
        double precioTotal = 1000.0;

        assertEquals(750.0, reservaService.calcularDescuento(precioTotal, "EFECTIVO"));
        assertEquals(900.0, reservaService.calcularDescuento(precioTotal, "TARJETA_DEBITO"));
        assertEquals(1000.0, reservaService.calcularDescuento(precioTotal, "TARJETA_CREDITO"));
    }

    @Test
    void calcularDescuentoTipoCliente() {
        double precioTotal = 1000.0;

        assertEquals(1000.0, reservaService.calcularDescuentoTipoCliente(precioTotal, "PASAPORTE", LocalDate.now()));
        assertEquals(900.0, reservaService.calcularDescuentoTipoCliente(precioTotal, "DNI", LocalDate.of(2024, 3, 1)));
        assertEquals(1000.0*0.85, reservaService.calcularDescuentoTipoCliente(precioTotal, "CUIT", LocalDate.now()));
        assertEquals(1000.0*0.9, reservaService.calcularDescuentoTipoCliente(precioTotal, "CUIT", LocalDate.of(2024, 1, 1)));
    }

    @Test
    void validarEntradas() {
        requestPostDto validRequest = new requestPostDto(1, "DNI", "SIMPLE", LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), "EFECTIVO");

        boolean result = reservaService.validarEntradas(validRequest);

        assertTrue(result);
    }

    @Test
    void validarEntradas_fechaInvalida() {
        requestPostDto validRequest = new requestPostDto(1, "DNI", "SIMPLE", LocalDate.now().plusDays(-1), LocalDate.now().plusDays(5), "EFECTIVO");

        boolean result = reservaService.validarEntradas(validRequest);

        assertFalse(result);
    }

    @Test
    void validarEntradas_fechaInvalida_rango() {
        requestPostDto validRequest = new requestPostDto(1, "DNI", "SIMPLE", LocalDate.now().plusDays(4), LocalDate.now().plusDays(2), "EFECTIVO");

        boolean result = reservaService.validarEntradas(validRequest);

        assertFalse(result);
    }

    @Test
    void validarEntradas_tipoInvalido() {
        requestPostDto validRequest = new requestPostDto(1, "DNI", "INVALID", LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), "EFECTIVO");

        boolean result = reservaService.validarEntradas(validRequest);

        assertFalse(result);
    }

    @Test
    void validarEntradas_clienteInvalido() {
        requestPostDto validRequest = new requestPostDto(1, "INVALID", "SIMPLE", LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), "EFECTIVO");

        boolean result = reservaService.validarEntradas(validRequest);

        assertFalse(result);
    }

    @Test
    void validarEntradas_pagoInvalido() {
        requestPostDto validRequest = new requestPostDto(1, "DNI", "SIMPLE", LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), "INVALID");

        boolean result = reservaService.validarEntradas(validRequest);

        assertFalse(result);
    }
}
