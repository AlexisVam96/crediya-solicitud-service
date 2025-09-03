package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.CreateSolicitudDto;
import co.com.crediya.api.dto.SolicitudDto;
import co.com.crediya.model.solicitud.Solicitud;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudDtoMapperTest {


    private final SolicitudDtoMapper mapper = Mappers.getMapper(SolicitudDtoMapper.class);

    @Test
    void toModel_shouldMapCreateUserDtoToUser() {
        CreateSolicitudDto dto = new CreateSolicitudDto();
        dto.setIdSolicitud(1);
        dto.setDocumentNumber("76584321");
        dto.setEmail("john.doe@example.com");
        dto.setIdTipoPrestamo(1);

        Solicitud solicitud = mapper.toModel(dto);

        assertThat(solicitud).isNotNull();
        assertThat(solicitud.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(solicitud.getDocumentNumber()).isEqualTo("76584321");
        assertThat(solicitud.getIdEstado()).isEqualTo(1);

    }

    @Test
    void toResponse_shouldMapSolicitudToUserDto() {
        Solicitud solicitud = new Solicitud();
        solicitud.setIdSolicitud(1);
        solicitud.setDocumentNumber("76584321");
        solicitud.setEmail("john.doe@example.com");
        solicitud.setIdEstado(1);
        solicitud.setIdTipoPrestamo(1);

        SolicitudDto dto = mapper.toResponse(solicitud);

        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(dto.getDocumentNumber()).isEqualTo("76584321");
    }

    @Test
    void toResponse_shouldMapUserListToUserDtoList() {
        Solicitud solicitud = new Solicitud();
        solicitud.setIdSolicitud(1);
        solicitud.setDocumentNumber("76584321");
        solicitud.setEmail("john.doe@example.com");
        solicitud.setIdEstado(1);
        solicitud.setIdTipoPrestamo(1);

        List<SolicitudDto> dtos = mapper.toResponseList(Collections.singletonList(solicitud));

        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getEmail()).isEqualTo("john.doe@example.com");
    }



}