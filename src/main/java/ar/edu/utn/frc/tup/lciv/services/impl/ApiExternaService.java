package ar.edu.utn.frc.tup.lciv.services.impl;

import ar.edu.utn.frc.tup.lciv.dtos.Disponibilidad;
import ar.edu.utn.frc.tup.lciv.dtos.Precio;
import ar.edu.utn.frc.tup.lciv.services.IApiExternaService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
public class ApiExternaService implements IApiExternaService {

    private final RestTemplate restTemplate;
//    private final String url = "http://localhost:8080/habitacion";
    private final String url = "http://java-api:8080/habitacion";

    private static final String RESILIENCE4J_INSTANCE_NAME = "microCircuitBreaker";

    public Integer counter = 0;

    @Autowired
    public ApiExternaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @CircuitBreaker(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = "fallbackGetDisponibilidad")
    public Disponibilidad getDisponibilidad(Integer hotel_id, String tipo_habitacion, LocalDate fecha_desde, LocalDate fecha_hasta) {
        counter++;
        System.out.println("Execution N: " + counter + " - Calling API");
        return restTemplate.getForEntity(url + "/disponibilidad?hotel_id=" + hotel_id
                + "&tipo_habitacion=" + tipo_habitacion + "&fecha_desde=" + fecha_desde + "&fecha_hasta=" + fecha_hasta,
                Disponibilidad.class).getBody();
    }

    @Override
    @CircuitBreaker(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = "fallbackGetPrecio")
    public Precio getPrecio(Integer hotel_id, String tipo_habitacion) {
        counter++;
        System.out.println("Execution N: " + counter + " - Calling API");
        return restTemplate.getForEntity(url + "/precio?hotel_id=" + hotel_id + "&tipo_habitacion=" + tipo_habitacion,
                Precio.class).getBody();
    }

    public Disponibilidad fallbackGetDisponibilidad(Exception ex) {
        counter++;
        System.out.println("Execution N: " + counter + " - Fallback method");
        throw new RuntimeException("Error manejado en el Fallback method");
    }

    public Precio fallbackGetPrecio(Exception ex) {
        counter++;
        System.out.println("Execution N: " + counter + " - Fallback method");
        throw new RuntimeException("Error manejado en el Fallback method");
    }

}
