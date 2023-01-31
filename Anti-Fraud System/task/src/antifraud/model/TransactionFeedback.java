package antifraud.model;

import antifraud.model.enums.TransactionResult;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TransactionFeedback {
    @NotNull
    Long transactionId;
    TransactionResult feedback;
}
