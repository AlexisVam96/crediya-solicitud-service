package co.com.crediya.model.sqs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DeptCapacityResponse {

    private Integer idSolicitud;
    private String resultado;
    private BigDecimal cuotaMensual;
    private BigDecimal capacidadDisponible;
}
