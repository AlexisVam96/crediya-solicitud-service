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

    private Integer idSolicitud;
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private Integer idEstado;
    private Integer idTipoPrestamo;
    private String documentNumber;

    private String nameUser;
    private BigDecimal baseSalary;
    private BigDecimal interestRate;
}
