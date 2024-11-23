package ar.edu.utn.frc.tup.lciv.services.impl;

import ar.edu.utn.frc.tup.lciv.dtos.Disponibilidad;
import ar.edu.utn.frc.tup.lciv.dtos.Precio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApiExternaServiceTest {

    @Test
    public void test_valid_input_returns_disponibilidad() {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        ApiExternaService apiExternaService = new ApiExternaService(restTemplate);
        Integer hotelId = 1;
        String tipoHabitacion = "SIMPLE";
        LocalDate fechaDesde = LocalDate.of(2023, 10, 1);
        LocalDate fechaHasta = LocalDate.of(2023, 10, 10);
        Disponibilidad expectedDisponibilidad = new Disponibilidad(tipoHabitacion, hotelId, fechaDesde, fechaHasta, true);

        Mockito.when(restTemplate.getForEntity(
                        Mockito.anyString(), Mockito.eq(Disponibilidad.class)))
                .thenReturn(new ResponseEntity<>(expectedDisponibilidad, HttpStatus.OK));

        Disponibilidad result = apiExternaService.getDisponibilidad(hotelId, tipoHabitacion, fechaDesde, fechaHasta);

        assertNotNull(result);
        assertEquals(expectedDisponibilidad, result);
    }

    // Handles null values for hotel_id, tipo_habitacion, fecha_desde, or fecha_hasta
    @Test
    public void test_null_values_handling() {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        ApiExternaService apiExternaService = new ApiExternaService(restTemplate);

        Assertions.assertThrows(NullPointerException.class, () -> {
            apiExternaService.getDisponibilidad(null, "SIMPLE", LocalDate.of(2023, 10, 1), LocalDate.of(2023, 10, 10));
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
            apiExternaService.getDisponibilidad(1, null, LocalDate.of(2023, 10, 1), LocalDate.of(2023, 10, 10));
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
            apiExternaService.getDisponibilidad(1, "SIMPLE", null, LocalDate.of(2023, 10, 10));
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
            apiExternaService.getDisponibilidad(1, "SIMPLE", LocalDate.of(2023, 10, 1), null);
        });
    }

    // Retrieves a Precio object for valid hotel_id and tipo_habitacion
    @Test
    public void test_get_precio_valid_input() {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        ApiExternaService apiExternaService = new ApiExternaService(restTemplate);
        Integer hotelId = 1;
        String tipoHabitacion = "SIMPLE";
        Precio expectedPrecio = new Precio(hotelId, tipoHabitacion, 100.0);

        Mockito.when(restTemplate.getForEntity(
                        Mockito.eq("http://localhost:8080/habitacion/precio?hotel_id=1&tipo_habitacion=SIMPLE"),
                        Mockito.eq(Precio.class)))
                .thenReturn(new ResponseEntity<>(expectedPrecio, HttpStatus.OK));

        Precio actualPrecio = apiExternaService.getPrecio(hotelId, tipoHabitacion);

        assertNotNull(actualPrecio);
        assertEquals(expectedPrecio, actualPrecio);
    }

    // Handles null or invalid hotel_id gracefully
    @Test
    public void test_get_precio_invalid_hotel_id() {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        ApiExternaService apiExternaService = new ApiExternaService(restTemplate);
        Integer hotelId = null;
        String tipoHabitacion = "SIMPLE";

        Mockito.when(restTemplate.getForEntity(
                        Mockito.anyString(),
                        Mockito.eq(Precio.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        Assertions.assertThrows(HttpClientErrorException.class, () -> {
            apiExternaService.getPrecio(hotelId, tipoHabitacion);
        });
    }

    @Test
    public void test_fallback_increments_counter() {
        ApiExternaService service = new ApiExternaService(new RestTemplate());
        int initialCounter = service.counter;
        try {
            service.fallbackGetDisponibilidad(null);
        } catch (RuntimeException e) {
            // Expected exception
        }
        assertEquals(initialCounter + 1, service.counter);
    }

    // Exception passed to fallback method is null
    @Test
    public void test_fallback_exception_is_null() {
        ApiExternaService service = new ApiExternaService(new RestTemplate());
        Exception exception = null;
        try {
            service.fallbackGetDisponibilidad(exception);
        } catch (RuntimeException e) {
            assertNotNull(e);
            assertEquals("Error manejado en el Fallback method", e.getMessage());
        }
    }

    @Test
    public void test_counter_increment_on_fallback() {
        ApiExternaService service = new ApiExternaService(new RestTemplate());
        int initialCounter = service.counter;
        try {
            service.fallbackGetPrecio(new Exception("Test Exception"));
        } catch (RuntimeException e) {
            // Expected exception
        }
        assertEquals(initialCounter + 1, service.counter);
    }

    // Handles null exception input gracefully
    @Test
    public void test_null_exception_handling() {
        ApiExternaService service = new ApiExternaService(new RestTemplate());
        int initialCounter = service.counter;
        try {
            service.fallbackGetPrecio(null);
        } catch (RuntimeException e) {
            // Expected exception
        }
        assertEquals(initialCounter + 1, service.counter);
    }
}