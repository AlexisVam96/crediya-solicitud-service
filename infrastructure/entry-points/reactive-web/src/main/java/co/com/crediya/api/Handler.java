package co.com.crediya.api;

import co.com.crediya.api.dto.CreateSolicitudDto;
import co.com.crediya.api.dto.SolicitudDto;
import co.com.crediya.api.mapper.SolicitudDtoMapper;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final SolicitudUseCase solicitudUseCase;
    private final SolicitudDtoMapper solicitudDtoMapper;

    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {
        return solicitudUseCase.getAllSolicitudes()
                .collectList()
                .map(solicitudDtoMapper::toResponseList)
                .flatMap(solicitudDtoList -> ServerResponse.ok().bodyValue(solicitudDtoList));
    }

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateSolicitudDto.class)
                .map(solicitudDtoMapper::toModel)
                .flatMap(solicitudUseCase::createSolicitud)
                .map(solicitudDtoMapper::toResponse)
                .flatMap(solicitudDto -> ServerResponse.ok().bodyValue(solicitudDto));
    }

}
