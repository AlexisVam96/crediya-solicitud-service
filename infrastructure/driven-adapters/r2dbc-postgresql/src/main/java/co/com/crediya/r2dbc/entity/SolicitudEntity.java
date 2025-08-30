package co.com.crediya.r2dbc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "solicitud_entity")
public class SolicitudEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column("id_solicitud")
    private Integer idSolicitud;

    private BigDecimal monto;
    private Integer plazo;
    private String email;

    @Column("id_estado")
    private Integer idEstado;

    @Column("id_tipo_prestamo")
    private Integer idTipoPrestamo;

    private String documentNumber;

}
