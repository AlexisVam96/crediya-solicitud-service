package co.com.crediya.model.user;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class User {

    private Integer idUser;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String address;
    private String phoneNumber;
    private String email;
    private BigDecimal salary;
    private Integer idRole;
    private String documentNumber;
}
