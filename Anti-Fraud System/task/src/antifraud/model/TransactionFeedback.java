package antifraud.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TransactionFeedback {
    @NotNull
    Long transactionId;
    @NotEmpty
    String feedback;
}
