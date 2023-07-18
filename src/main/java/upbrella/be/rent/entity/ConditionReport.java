package upbrella.be.rent.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class ConditionReport {

    @Id
    @GeneratedValue
    private long id;
    @OneToOne
    @JoinColumn(name = "history_id")
    private History history;
    private String content;
    private String etc;
}
