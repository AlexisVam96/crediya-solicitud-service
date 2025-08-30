package co.com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolicitudDto {

    @Schema(hidden = true)
    private Integer idSolicitud;

    @Schema(description = "Monto de la solicitud", example = "1000.50")
    private BigDecimal monto;

    @Schema(description = "Plazo de la solicitud", example = "10")
    private Integer plazo;

    @Schema(description = "Email de la solicitud", example = "demo@gmail.com")
    private String email;

    @Schema(description = "IdEstado de la solicitud", example = "1")
    private Integer idEstado;

    @Schema(description = "IdTipoPrestamo de la solicitud", example = "1")
    private Integer idTipoPrestamo;

    @Schema(description = "Número de documento del solicitante", example = "123456789")
    private String documentNumber;
}
