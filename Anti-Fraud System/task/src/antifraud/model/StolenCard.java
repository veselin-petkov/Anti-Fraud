package antifraud.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class StolenCard {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    String number;


    public StolenCard(Long id, String number) {
        this.id = id;
        this.number = number;
    }

    public StolenCard() {

    }
}
