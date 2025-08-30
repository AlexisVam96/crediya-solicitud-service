package co.com.crediya.consumer.config;
import lombok.Data;

import java.time.Instant;

@Data
public class ErrorResponse {

    private Instant timestamp;

    private String error;

    private String message;

    private String type;

}

