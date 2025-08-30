package co.com.crediya.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "estado_entity")
public class EstadoEntity {

    @Id
    @Column("id_estado")
    private Integer idEstado;
    private String nombre;
    private String descripcion;
}
