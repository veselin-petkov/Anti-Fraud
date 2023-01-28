package antifraud.model;

import antifraud.model.enums.Region;
import antifraud.model.enums.TransactionResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;
    Long amount;
    String ip;
    String number;
    Region region;
    LocalDateTime date;
    TransactionResult result;
    String feedback;
}
