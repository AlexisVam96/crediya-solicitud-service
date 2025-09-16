package co.com.crediya.sqsdebtcapacity.config;

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
@ConfigurationProperties(prefix = "adapter.sqs.debt-capacity")
public class SQSSenderDeptCapacityProperties {
    private String region;
    private String queueUrl;
    private String endpoint;
    private String replyQueueUrl;
}
