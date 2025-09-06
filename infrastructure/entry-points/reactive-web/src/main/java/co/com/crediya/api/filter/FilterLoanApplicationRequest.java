package co.com.crediya.api.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterLoanApplicationRequest {

    private Integer page;
    private Integer size;
    private String statusLoanApplication;

}
