package antifraud.model.request;

import antifraud.model.enums.Region;
import antifraud.validation.IpAddress;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;
    @Positive
    @NotNull
    Long amount;
    @IpAddress
    String ip;
    @CreditCardNumber
    String number;
    @NotNull
    Region region;
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    LocalDateTime date;
}
