package antifraud.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class SuspiciousIp {
    @Id
    @GeneratedValue
    Long id;
    @Column(nullable = false)
    String ip;
}
