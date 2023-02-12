package antifraud.model;

import antifraud.model.enums.Region;
import antifraud.model.enums.TransactionResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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
