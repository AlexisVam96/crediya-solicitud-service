package co.com.crediya.api.exception;

import co.com.crediya.model.exception.LoanApplicationCustomerException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options){
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        Throwable error = getError(request);
        errorAttributes.put("timestamp", Instant.now().toString());
        errorAttributes.put("path", request.path());
        errorAttributes.put("message", error.getMessage());

        if(error instanceof LoanApplicationCustomerException ex) {
            errorAttributes.put("type", ex.getType());
        }

        return errorAttributes;
    }

}
