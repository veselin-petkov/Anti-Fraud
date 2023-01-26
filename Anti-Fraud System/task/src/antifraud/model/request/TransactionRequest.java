package antifraud.model.request;

import antifraud.model.enums.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    @Positive
    @NotNull
    Long amount;
    @NotEmpty
    String ip;
    @NotEmpty
    String number;
    @NotEmpty
    Region region;
    @NotEmpty
    Date date;
}
