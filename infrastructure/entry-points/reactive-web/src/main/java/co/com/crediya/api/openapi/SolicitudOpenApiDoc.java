package co.com.crediya.api.openapi;

import co.com.crediya.api.Handler;
import co.com.crediya.api.dto.CreateLoanApplicationRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

public interface SolicitudOpenApiDoc {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenPOSTUseCase",
                    operation = @Operation(
                            operationId = "listenPOSTCreateLoanApplication",
                            summary = "Crear solicitud",
                            requestBody = @RequestBody(
                                    description = "Nueva solicitud",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = CreateLoanApplicationRequestDto.class),
                                            examples = {
                                                    @ExampleObject(
                                                            name = "Crear Solicitud con Estado Inicial Pendiente",
                                                            value = "{\n" +
                                                                    "  \"monto\": 1000.50,\n" +
                                                                    "  \"plazo\": 10,\n" +
                                                                    "  \"email\": \"demo@gmail.com\",\n" +
                                                                    "  \"idTipoPrestamo\": 1,\n" +
                                                                    "  \"documentNumber\": \"77378856\"\n" +
                                                                    "}"
                                                    ),
                                                    @ExampleObject(
                                                            name = "Crear Solicitud con Documento Inválido",
                                                            value = "{\n" +
                                                                    "  \"monto\": 1000.50,\n" +
                                                                    "  \"plazo\": 10,\n" +
                                                                    "  \"email\": \"demo@gmail.com\",\n" +
                                                                    "  \"idTipoPrestamo\": 1,\n" +
                                                                    "  \"documentNumber\": \"123456789\"\n" +
                                                                    "}"
                                                    ),
                                                    @ExampleObject(
                                                            name = "Crear Solicitud con Tipo Prestamo Inválido",
                                                            value = "{\n" +
                                                                    "  \"monto\": 1000.50,\n" +
                                                                    "  \"plazo\": 10,\n" +
                                                                    "  \"email\": \"demo@gmail.com\",\n" +
                                                                    "  \"idTipoPrestamo\": 5,\n" +
                                                                    "  \"documentNumber\": \"77378856\"\n" +
                                                                    "}"
                                                    )
                                            }
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Creado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "listenGETLoanApplicationByStatus",
                    operation = @Operation(
                            operationId = "listenGETLoanApplicationByStatus",
                            summary = "Listar solicitudes por estado",
                            description = "Obtiene una lista paginada de solicitudes filtradas por estado.",
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "page",
                                            description = "Número de página",
                                            required = false,
                                            example = "0"
                                    ),
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "size",
                                            description = "Tamaño de página",
                                            required = false,
                                            example = "10"
                                    ),
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "status",
                                            description = "ID del estado de la solicitud",
                                            required = false,
                                            example = "1"
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Lista de solicitudes",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    examples = @ExampleObject(
                                                            name = "Lista de Solicitudes",
                                                            value = "[\n" +
                                                                    "  {\n" +
                                                                    "    \"idSolicitud\": 10,\n" +
                                                                    "    \"monto\": 1500.00,\n" +
                                                                    "    \"plazo\": 12,\n" +
                                                                    "    \"email\": \"jhon.doe@crediya.com\",\n" +
                                                                    "    \"idTipoPrestamo\": 1,\n" +
                                                                    "    \"documentNumber\": \"77283809\",\n" +
                                                                    "    \"estado\": \"Pendiente de revisión\"\n" +
                                                                    "  }\n" +
                                                                    "]"
                                                    )
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/calcular-capacidad",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenPOSTCalculateDebtCapacity",
                    operation = @Operation(
                            operationId = "listenPOSTCalculateDebtCapacity",
                            summary = "Calcular capacidad de endeudamiento",
                            requestBody = @RequestBody(
                                    description = "Datos para calcular capacidad de endeudamiento",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = CreateLoanApplicationRequestDto.class),
                                            examples = {
                                                    @ExampleObject(
                                                            name = "Calcular Capacidad",
                                                            value = "{\n" +
                                                                    "  \"monto\": 1500.00,\n" +
                                                                    "  \"plazo\": 12,\n" +
                                                                    "  \"email\": \"jhon.doe@crediya.com\",\n" +
                                                                    "  \"idTipoPrestamo\": 1,\n" +
                                                                    "  \"documentNumber\": \"77283809\"\n" +
                                                                    "}"
                                                    )
                                            }
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Capacidad calculada",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    examples = @ExampleObject(
                                                            name = "Respuesta Capacidad",
                                                            value = "{\n" +
                                                                    "  \"idSolicitud\": 10,\n" +
                                                                    "  \"monto\": 1500.00,\n" +
                                                                    "  \"plazo\": 12,\n" +
                                                                    "  \"email\": \"jhon.doe@crediya.com\",\n" +
                                                                    "  \"idTipoPrestamo\": 1,\n" +
                                                                    "  \"documentNumber\": \"77283809\",\n" +
                                                                    "}"
                                                    )
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    method = RequestMethod.PUT,
                    beanClass = Handler.class,
                    beanMethod = "listenPUTLoanApplicationByStatus",
                    operation = @Operation(
                            operationId = "listenPUTLoanApplicationByStatus",
                            summary = "Actualizar solicitud",
                            description = "Actualiza una solicitud existente.",
                            requestBody = @RequestBody(
                                    description = "Datos de la solicitud a actualizar",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = co.com.crediya.api.dto.SolicitudDto.class),
                                            examples = @ExampleObject(
                                                    name = "Actualizar Solicitud",
                                                    value = "{\n" +
                                                            "  \"idSolicitud\": 10,\n" +
                                                            "  \"monto\": 1500.00,\n" +
                                                            "  \"plazo\": 12,\n" +
                                                            "  \"email\": \"jhon.doe@crediya.com\",\n" +
                                                            "  \"idTipoPrestamo\": 1,\n" +
                                                            "  \"documentNumber\": \"77283809\",\n" +
                                                            "  \"estado\": \"Aprobado\"\n" +
                                                            "}"
                                            )
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Solicitud actualizada",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    examples = @ExampleObject(
                                                            name = "Respuesta Actualización",
                                                            value = "{\n" +
                                                                    "  \"idSolicitud\": 10,\n" +
                                                                    "  \"monto\": 1500.00,\n" +
                                                                    "  \"plazo\": 12,\n" +
                                                                    "  \"email\": \"jhon.doe@crediya.com\",\n" +
                                                                    "  \"idTipoPrestamo\": 1,\n" +
                                                                    "  \"documentNumber\": \"77283809\",\n" +
                                                                    "  \"estado\": \"Aprobado\"\n" +
                                                                    "}"
                                                    )
                                            )
                                    )
                            }
                    )
            )
    })
    RouterFunction<ServerResponse> routerFunction(Handler handler);
}
