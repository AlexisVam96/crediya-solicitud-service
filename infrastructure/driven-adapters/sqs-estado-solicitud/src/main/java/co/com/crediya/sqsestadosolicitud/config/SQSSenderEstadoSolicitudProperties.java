package co.com.crediya.sqsestadosolicitud.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "adapter.sqs.estado-solicitud")
public class SQSSenderEstadoSolicitudProperties {
    private String region;
    private String queueUrl;
    private String endpoint;
}
