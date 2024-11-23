package ar.edu.utn.frc.tup.lciv.controllers;

import ar.edu.utn.frc.tup.lciv.dtos.reserva.requestPostDto;
import ar.edu.utn.frc.tup.lciv.models.Reserva;
import ar.edu.utn.frc.tup.lciv.services.impl.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReservaControllerTest {

    @Test
    public void test_create_reserva_with_valid_input() {
        // Arrange
        ReservaService reservaService = mock(ReservaService.class);
        ReservaController reservaController = new ReservaController(reservaService);
        requestPostDto requestDto = new requestPostDto(1, "DNI", "SIMPLE", LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), "EFECTIVO");
        Reserva expectedReserva = new Reserva(1, "DNI", 1, "SIMPLE", LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), "EXITOSA", 1000.0, "EFECTIVO");

        when(reservaService.createReserva(requestDto)).thenReturn(expectedReserva);

        // Act
        ResponseEntity<Reserva> response = reservaController.createReserva(requestDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedReserva, response.getBody());
    }

    @Test
    public void test_create_reserva_with_invalid_input() {
        // Arrange
        ReservaService reservaService = mock(ReservaService.class);
        ReservaController reservaController = new ReservaController(reservaService);
        requestPostDto invalidRequestDto = new requestPostDto(1, "INVALID", "SIMPLE", LocalDate.now().minusDays(1), LocalDate.now().plusDays(5), "EFECTIVO");

        when(reservaService.createReserva(invalidRequestDto)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos incorrectos"));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            reservaController.createReserva(invalidRequestDto);
        });
    }

    @Test
    public void test_get_reserva_by_id_success() {
        ReservaService reservaService = mock(ReservaService.class);
        ReservaController reservaController = new ReservaController(reservaService);
        Reserva expectedReserva = new Reserva(1, "DNI", 101, "SIMPLE", LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), "EXITOSA", 1000.0, "EFECTIVO");

        when(reservaService.getReservaById(1)).thenReturn(expectedReserva);

        ResponseEntity<Reserva> response = reservaController.getReservaById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedReserva, response.getBody());
    }

    @Test
    public void test_get_reserva_by_id_not_found() {
        ReservaService reservaService = mock(ReservaService.class);
        ReservaController reservaController = new ReservaController(reservaService);

        when(reservaService.getReservaById(999)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            reservaController.getReservaById(999);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Reserva no encontrada", exception.getReason());
    }
}