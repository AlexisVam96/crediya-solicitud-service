package co.com.crediya.r2dbc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tipo_prestamo_entity")
public class TipoPrestamoEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Integer id_tipo_prestamo;
    private String nombre;
    private BigDecimal monto_minimo;
    private BigDecimal monto_maximo;
    private BigDecimal tasa_interes;
    private Boolean validacion_automatica;
}
