package upbrella.be.user.entity;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class User {

    @Id
    @GeneratedValue
    private int id;
    private long socialId;
    private String name;
    private String phoneNumber;
    private boolean adminStatus;
}
