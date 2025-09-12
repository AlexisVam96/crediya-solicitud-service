package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.CreateLoanApplicationRequestDto;
import co.com.crediya.api.dto.CreateLoanApplicationResponseDto;
import co.com.crediya.api.dto.SolicitudDto;
import co.com.crediya.model.solicitud.Solicitud;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SolicitudDtoMapper {

    SolicitudDto toResponse(Solicitud solicitud);

    List<SolicitudDto> toResponseList(List<Solicitud> solicitudes);

    Solicitud toModel(CreateLoanApplicationRequestDto createSolicitudDto);

    Solicitud toModel(SolicitudDto solicitudDto);

    CreateLoanApplicationResponseDto toResponseCreate(Solicitud solicitud);
}
