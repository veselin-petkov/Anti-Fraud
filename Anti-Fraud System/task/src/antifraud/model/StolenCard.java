package antifraud.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class StolenCard {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    String number;

}
