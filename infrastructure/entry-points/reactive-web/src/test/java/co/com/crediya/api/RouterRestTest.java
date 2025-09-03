package co.com.crediya.api;

import co.com.crediya.api.dto.SolicitudDto;
import co.com.crediya.api.mapper.SolicitudDtoMapper;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SolicitudUseCase solicitudUseCase;

    @MockBean
    private SolicitudDtoMapper solicitudDtoMapper;

    private SolicitudDto solicitudDto() {
        SolicitudDto dto = new SolicitudDto();
        dto.setEmail("john.doe@example.com");
        // Set other required fields as needed
        return dto;
    }

    private Solicitud solicitud() {
        Solicitud solicitud = new Solicitud();
        solicitud.setEmail("john.doe@example.com");
        // Set other required fields as needed
        return solicitud;
    }

    @Test
    void testListenGETAllSolicitudes() {
        // Arrange
        Solicitud solicitud = solicitud();
        SolicitudDto dto = solicitudDto();

        when(solicitudUseCase.getAllSolicitudes()).thenReturn(Flux.just(solicitud));
        when(solicitudDtoMapper.toResponseList(List.of(solicitud))).thenReturn(Collections.singletonList(dto));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/solicitudes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SolicitudDto.class)
                .value(userList -> {
                    Assertions.assertThat(userList).isNotEmpty();
                    Assertions.assertThat(userList)
                            .extracting(SolicitudDto::getEmail)
                            .contains("john.doe@example.com");
                });
    }

    @Test
    void testListenPOSTSaveUser_shouldReturnOkResponse() {
        SolicitudDto dto = solicitudDto();
        Solicitud solicitud = solicitud();

        when(solicitudDtoMapper.toModel(any(SolicitudDto.class))).thenReturn(solicitud);
        when(solicitudUseCase.createSolicitud(any(Solicitud.class))).thenReturn(Mono.just(solicitud));
        when(solicitudDtoMapper.toResponse(any(Solicitud.class))).thenReturn(dto);

        webTestClient.post()
                .uri("/api/v1/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SolicitudDto.class)
                .value(response -> {
                    Assertions.assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
                });
    }


}
