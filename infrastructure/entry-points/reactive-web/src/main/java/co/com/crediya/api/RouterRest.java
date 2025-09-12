package co.com.crediya.api;

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
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "listenGETUseCase",
                    operation = @Operation(
                        operationId = "listenGETAllLoanApplications",
                        summary = "Listar solicitudes",
                        responses = {
                                @ApiResponse(responseCode = "200", description = "OK")
                        }
                    )
            ),
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
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET("/api/v1/solicitud"), handler::listenGETLoanApplicationByStatus)
                .andRoute(POST("/api/v1/solicitud"), handler::listenPOSTUseCase)
                .andRoute(PUT("/api/v1/solicitud"), handler::listenPUTLoanApplicationByStatus);
    }
}
