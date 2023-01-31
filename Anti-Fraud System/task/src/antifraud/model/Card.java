package antifraud.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Card {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    String number;
    @Column(name = "max_allowed")
    int maxAllowed;
    @Column(name = "max_manual")
    int maxManual;
}
