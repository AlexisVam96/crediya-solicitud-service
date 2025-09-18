package co.com.crediya.api;

import co.com.crediya.api.dto.CreateLoanApplicationRequestDto;
import co.com.crediya.api.openapi.SolicitudOpenApiDoc;
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
public class RouterRest implements SolicitudOpenApiDoc {

    @Bean
    @Override
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET("/api/v1/solicitud"), handler::listenGETLoanApplicationByStatus)
                .andRoute(POST("/api/v1/solicitud"), handler::listenPOSTUseCase)
                .andRoute(PUT("/api/v1/solicitud"), handler::listenPUTLoanApplicationByStatus)
                .andRoute(POST("/api/v1/calcular-capacidad"), handler::listenPOSTCalculateDebtCapacity);
    }
}
