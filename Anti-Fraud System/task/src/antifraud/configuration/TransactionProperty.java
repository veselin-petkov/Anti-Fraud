package antifraud.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Positive;

@Slf4j
@ConfigurationProperties(prefix = "transaction.values")
@Getter
@Setter
@AllArgsConstructor
@ConstructorBinding
@Validated
public class TransactionProperty {
    @Positive
    private int initialMaxAllowed;
    @Positive
    private int initialMaxManual;
    @Positive
    private double currentLimitModifier;
    @Positive
    private double valueFromTransactionModifier;
    @Positive
    private int regionAndIpLimit;
}
