package co.com.crediya.model.solicitud;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitud {

    private Integer id_solicitud;
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private Integer id_estado;
    private Integer id_tipo_prestamo;
}
