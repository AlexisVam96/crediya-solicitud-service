package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.CreateSolicitudDto;
import co.com.crediya.api.dto.SolicitudDto;
import co.com.crediya.model.solicitud.Solicitud;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SolicitudDtoMapper {

    SolicitudDto toResponse(Solicitud solicitud);

    List<SolicitudDto> toResponseList(List<Solicitud> solicitudes);

    Solicitud toModel(CreateSolicitudDto createSolicitudDto);
}
