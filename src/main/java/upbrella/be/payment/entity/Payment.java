package upbrella.be.payment.entity;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Payment {

    @Id
    @GeneratedValue
    private long id;
}
