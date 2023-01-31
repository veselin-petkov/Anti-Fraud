package antifraud.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(indexes = {@Index( name = "idx_identity_ip_unq",columnList = "ip",unique = true)})
public class SuspiciousIp {
    @Id
    @GeneratedValue
    Long id;
    @Column(nullable = false)
    String ip;
}
