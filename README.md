# API REST de Reserva de Habitaciones de Hotel

## Contexto del Negocio:
Imagine que está trabajando para un exclusivo hotel llamado "Grand Paradise Hotel & Spa". 
Este hotel ofrece tres tipos de habitaciones: Simple, Doble y Triple. 
La administración del hotel desea implementar un sistema que permita a los clientes verificar 
la disponibilidad de habitaciones en una fecha determinada y realizar reservas de manera eficiente.

## Enpoint a diseñar

### Crear reserva

- Endpoint: ```/reserva```
- Método: ```POST```
- Request:
  ```
  {
    "id_hotel": 123,
    "id_cliente": "AAC123987",
    "tipo_habitacion": "SIMPLE",
    "fecha_ingreso": "2024-12-25",
    "fecha_salida": "2024-12-28",
    "medio_pago": "EFECTIVO"
  }
- Respuesta:
  ```
  {
    "id_reserva": 12,
    "id_cliente": "AAC123987",
    "id_hotel": 123,
    "tipo_habitacion": "SIMPLE",
    "fecha_ingreso": "2024-12-25",
    "fecha_salida": "2024-12-28",
    "estado_reserva": "EXITOSA",
    "precio": 1250.33,
    "medio_pago": "EFECTIVO"
  }
  ```

### Recuperar información de la reserva
- Endpoint de la API Externa: ```/reserva/{id_reserva}```
- Método: ```GET```
- Respuesta:
  ```
  {
    "id_reserva": 12,
    "id_cliente": "AAC123987",
    "id_hotel": 123,
    "tipo_habitacion": "SIMPLE",
    "fecha_ingreso": "2024-12-25",
    "fecha_salida": "2024-12-28",
    "estado_reserva": "EXITOSA",
    "precio": 1250.33,
    "medio_pago": "EFECTIVO"
  }
  ```

## Reglas del Negocio:

### Información de la API Externa:

La api externa debe ser levantada via docker con la siguiente imagen:

``` danicattaneob/labo-iv-final-2023-12-19```
``` danicattaneob/labo-iv-final-2023-12-19:1.0.0```

Y una vez levantada se puede acceder a su documentación en la url:

```http://localhost:8080/docs#/```

Para llevar a cabo la gestión de reservas de habitaciones de hotel de manera efectiva, 
su aplicación interactuará con una API externa que proporcionará información clave. 
A continuación, se detalla la información que se consultará desde la API externa:

#### Disponibilidad de Habitaciones:

Se deberá consultar la disponibilidad de todos los días de la estadía

- Endpoint: ```/habitacion/disponibilidad```
- Método: ```GET```
- Query parameters:
  - hotel_id: id numérico
  - tipo_habitacion: ```SIMPLE```/```DOBLE```/```TRIPLE```
  - fecha (formato: ```YYYY-MM-DD```)
- Ejemplo -> ```/habitacion/disponibilidad?hotel_id=1&tipo_habitacion=DOBLE&fecha_desde=2023-12-25&fecha_hasta=2023-12-26```
- Respuesta:
  ```
  {
    "id_hotel": 123,
    "tipo_habitacion": "SIMPLE",
    "fecha": "2024-12-25",
    "disponible": true,
  }
  ```

#### Precio de habitación
- Endpoint de la API Externa: ```/habitacion/precio```
- Método: ```GET```
- Parámetros de Consulta:
    - hotel_id: id numérico
    - tipo_habitacion: ```SIMPLE```/```DOBLE```/```TRIPLE```
- Respuesta:
  ```
  {
    "id_hotel": 123,
    "tipo_habitacion": "TRIPLE",
    "precio_lista": 250.25,
  }
  ```

### Tipos de Habitaciones:

- Simple (SIMPLE): Habitación para una persona.
- Doble (DOBLE): Habitación para dos personas.
- Triple (TRIPLE): Habitación para tres personas.


### Identificación del cliente:

El id del cliente puede ser de los siguientes tipo:
- PASAPORTE: Si el cliente es extranjero
- DNI: Si el cliente es Argentino
- CUIT: Si el cliente es una empresa

### Variación de Precios según Temporada:

- Temporada Alta (enero, febrero, julio, agosto): +30% al precio de lista.
- Temporada Baja (marzo, abril, octubre, noviembre): -10% al precio de lista.
- Temporada Media (junio, septiembre, diciembre): Precio de lista.

Si una estadia forma parte de mas de una temporada se toma el mayor precio para el calculo de toda la estadía

### Descuentos según Medio de Pago:

- En efectivo (EFECTIVO): 25% de descuento sobre el precio segun temporada.
- En débito (TARJETA_DEBITO): 10% de descuento sobre el precio segun temporada.
- en credito (TARJETA_CREDITO) no se realiza descuento

### Tipo de Cliente:
- Para los clientes que contratan el servicio bajo el plan empresa, se aplica un 10% de descuento 
sobre el valor final. En temporada baja, este descuento se incrementa al 15%.
- Para los clientes particulares que realizan reservas durante la temporada baja, 
se ofrece un descuento adicional del 10%.

### Almacenamiento de la reserva
Si las validaciones son exitosas y luego de hacer el calculo final del precio 
debe almacenarse en una BD la información de la reserva.

### Manejo de Errores:

- Si la API de disponibilidad falla, el estado de la reserva debe ser PENDIENTE.
- Se deberá validar las entradas recibidas (no se pueden fechas pasadas, el intervalo debe ser correcto, 
el tipo de habitación valido, etc.) y en caso de que se envie un dato incorrecto debe devolver status code 400

### Docker
- Crear un archivo Dockerfile para dockerizar el proyecto
- Crear un archivo docker-compose que permita levantar tanto este servicio como el servicio externo

### Testing
- Es obligatorio la creación de testing unitario sobre la lógica de negocio de todo el proyecto
  
## Puntajes

- Validación de parametros **(10 pts)**
- Calculo de precio **(30 pts)**
- Guardado de reserva **(10 pts)**
- Buscar reserva **(10 pts)**
- Testing **(30 pts)**
- Docker y Docker Compose **(10 pts)**