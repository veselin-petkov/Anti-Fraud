package antifraud.model;

import antifraud.model.enums.Region;
import antifraud.model.enums.TransactionResult;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long transactionId;
    Long amount;
    String ip;
    String number;
    Region region;
    LocalDateTime date;
    TransactionResult result;
    TransactionResult feedback;

    public String getFeedback() {
        return feedback == null ? "" : feedback.name();
    }
}
