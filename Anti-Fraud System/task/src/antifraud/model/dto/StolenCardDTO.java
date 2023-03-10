package antifraud.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class StolenCardDTO {
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    Long id;
    @NotEmpty
    @CreditCardNumber
    String number;
}
