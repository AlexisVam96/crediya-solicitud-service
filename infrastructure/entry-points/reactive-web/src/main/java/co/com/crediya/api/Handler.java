package co.com.crediya.api;

import co.com.crediya.api.dto.CreateSolicitudDto;
import co.com.crediya.api.dto.SolicitudDto;
import co.com.crediya.api.filter.FilterLoanApplicationRequest;
import co.com.crediya.api.mapper.SolicitudDtoMapper;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
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

    public Mono<ServerResponse> listenGETLoanApplicationByStatus(ServerRequest serverRequest) {
        Integer page = Integer.parseInt(serverRequest.queryParam("page").orElse("0"));
        Integer size = Integer.parseInt(serverRequest.queryParam("size").orElse("10"));
        String statusLoanApplication = serverRequest.queryParam("status").orElse("1");
        return  solicitudUseCase.getLoanApplicationByStatus(
                    page, size, statusLoanApplication)
                    .collectList()
                    .map(solicitudDtoMapper::toResponseList)
                    .flatMap(solicitudDtoList -> ServerResponse.ok().bodyValue(solicitudDtoList));
    }

}
